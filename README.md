# WebSockets

WebSocket messaging that includes:

- raw WebSocket interactions
- WebSocket emulation through SockJS
- publish-subscribe messaging through STOMP as a sub-protocol over WebSocket

---

## Introduction to WebSocket

### HTTP Versus WebSocket

In HTTP and REST, an application is modeled as many URLs. To interact with the application, clients access those URLs,
request-response style. Servers route requests to the appropriate handler based on the HTTP URL, method, and headers.

By contrast, in WebSockets, there is usually only one URL for the initial connect. Subsequently, all application
messages flow on that same TCP connection. This points to an entirely different asynchronous, event-driven, messaging
architecture.

WebSocket is also a low-level transport protocol, which, unlike HTTP, does not prescribe any semantics to the content of
messages. That means that there is no way to route or process a message unless the client and the server agree on
message semantics.

WebSocket clients and servers can negotiate the use of a higher-level, messaging protocol (for example, STOMP), through
the Sec-WebSocket-Protocol header on the HTTP handshake request. In the absence of that, they need to come up with their
own conventions.

### When to Use WebSockets

It is the combination of low latency, high frequency, and high volume that make the best case for the use of WebSocket.

Keep in mind also that over the Internet, restrictive proxies that are outside of your control may preclude WebSocket
interactions, either because they are not configured to pass on the Upgrade header or because they close long-lived
connections that appear idle.

This means that the use of WebSocket for internal applications within the firewall is a more straightforward decision
than it is for public facing applications.

---

## WebSocket API

The Spring Framework provides a WebSocket API that you can use to write client- and server-side applications that handle
WebSocket messages.

### WebSocketHandler

When using the WebSocketHandler API directly vs indirectly, e.g. through the STOMP messaging, the application must
synchronize the sending of messages since the underlying standard WebSocket session (JSR-356) does not allow concurrent
sending. One option is to wrap the WebSocketSession with ConcurrentWebSocketSessionDecorator.

### WebSocket Handshake

The easiest way to customize the initial HTTP WebSocket handshake request is through a HandshakeInterceptor, which
exposes methods for “before” and “after” the handshake.

You can use such an interceptor to preclude the handshake or to make any attributes available to the WebSocketSession.

---

## SockJS Fallback

Over the public Internet, restrictive proxies outside your control may preclude WebSocket interactions, either because
they are not configured to pass on the Upgrade header or because they close long-lived connections that appear to be
idle.

The solution to this problem is WebSocket emulation — that is, attempting to use WebSocket first and then falling back
on HTTP-based techniques that emulate a WebSocket interaction and expose the same application-level API.

### Overview

The goal of SockJS is to let applications use a WebSocket API but fall back to non-WebSocket alternatives when necessary
at runtime, without the need to change application code.

---

## STOMP

The WebSocket protocol defines two types of messages (text and binary), but their content is undefined. The protocol
defines a mechanism for client and server to negotiate a sub-protocol (that is, a higher-level messaging protocol) to
use on top of WebSocket to define what kind of messages each can send, what the format is, the content of each message,
and so on. The use of a sub-protocol is optional but, either way, the client and the server need to agree on some
protocol that defines message content.

### Overview

When you use Spring’s STOMP support, the Spring WebSocket application acts as the STOMP broker to clients. Messages are
routed to @Controller message-handling methods or to a simple in-memory broker that keeps track of subscriptions and
broadcasts messages to subscribed users. You can also configure Spring to work with a dedicated STOMP broker (such as
RabbitMQ, ActiveMQ, and others) for the actual broadcasting of messages. In that case, Spring maintains TCP connections
to the broker, relays messages to it, and passes messages from it down to connected WebSocket clients. Thus, Spring web
applications can rely on unified HTTP-based security, common validation, and a familiar programming model for message
handling.

The meaning of a destination is intentionally left opaque in the STOMP spec. It can be any string, and it is entirely up
to STOMP servers to define the semantics and the syntax of the destinations that they support. It is very common,
however, for destinations to be path-like strings where /topic/.. implies publish-subscribe (one-to-many) and /queue/
implies point-to-point (one-to-one) message exchanges.

### Benefits

Using STOMP as a sub-protocol lets the Spring Framework and Spring Security provide a richer programming model versus
using raw WebSockets. The same point can be made about HTTP versus raw TCP and how it lets Spring MVC and other web
frameworks provide rich functionality.

The following is a list of benefits:

- No need to invent a custom messaging protocol and message format.
- STOMP clients, including a Java client in the Spring Framework, are available.
- You can (optionally) use message brokers (such as RabbitMQ, ActiveMQ, and others) to manage subscriptions and
  broadcast messages.
- Application logic can be organized in any number of @Controller instances and messages can be routed to them based on
  the STOMP destination header versus handling raw WebSocket messages with a single WebSocketHandler for a given
  connection.
- You can use Spring Security to secure messages based on STOMP destinations and message types.

### Flow of Messages

https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#websocket-stomp-message-flow

### Annotated Controllers

Applications can use annotated @Controller classes to handle messages from clients. Such classes can declare
@MessageMapping, @SubscribeMapping, and @ExceptionHandler methods, as described in the following topics:

You can use @MessageMapping to annotate methods that route messages based on their destination. It is supported at the
method level as well as at the type level. At the type level, @MessageMapping is used to express shared mappings across
all methods in a controller.

By default, the return value from a @MessageMapping method is serialized to a payload through a matching
MessageConverter and sent as a Message to the brokerChannel, from where it is broadcast to subscribers. The destination
of the outbound message is the same as that of the inbound message but prefixed with /topic.

You can use the @SendTo and @SendToUser annotations to customize the destination of the output message. @SendTo is used
to customize the target destination or to specify multiple destinations. @SendToUser is used to direct the output
message to only the user associated with the input message.

@SubscribeMapping is similar to @MessageMapping but narrows the mapping to subscription messages only.

When is this useful?

A client could also subscribe to some /app destination, and a controller could return a value in response to that
subscription without involving the broker without storing or using the subscription again (effectively a one-time
request-reply exchange). One use case for this is populating a UI with initial data on startup.

An application can use @MessageExceptionHandler methods to handle exceptions from @MessageMapping methods.

### Sending Messages

What if you want to send messages to connected clients from any part of the application? Any application component can
send messages to the brokerChannel. The easiest way to do so is to inject a SimpMessagingTemplate and use it to send
messages.

### Simple Broker

The built-in simple message broker handles subscription requests from clients, stores them in memory, and broadcasts
messages to connected clients that have matching destinations. The broker supports path-like destinations, including
subscriptions to Ant-style destination patterns.

### External Broker

The simple broker is great for getting started but supports only a subset of STOMP commands (it does not support acks,
receipts, and some other features), relies on a simple message-sending loop, and is not suitable for clustering. As an
alternative, you can upgrade your applications to use a full-featured message broker.

In effect, the broker relay enables robust and scalable message broadcasting.

---

## RabbitMQ

### Downloading and Installing RabbitMQ

`docker run --name rabbitmq -p 5672:5672 -p 15672:15672 -p 61613:61613 -d rabbitmq:3.9-management`

### UI

http://localhost:15672/#/

Username: guest

Password: guest

### Enable stomp plugin

`docker container exec -it rabbitmq bash`

`> rabbitmq-plugins list`

`> rabbitmq-plugins enable rabbitmq_stomp`

Note: main directory is /opt/rabbitmq

### Destinations

The STOMP specification does not prescribe what kinds of destinations a broker must support, instead the value of the
destination header in SEND and MESSAGE frames is broker-specific. The RabbitMQ STOMP adapter supports a number of
different destination types:

- `/queue` -- `SEND` and `SUBSCRIBE` to queues managed by the STOMP gateway;
- `/topic` -- `SEND` and `SUBSCRIBE` to transient and durable topics;

#### Queue Destinations

For simple queues, destinations of the form `/queue/<name>` can be used.

Queue destinations deliver each message to at most one subscriber. Messages sent when no subscriber exists will be
queued until a subscriber connects to the queue.

#### Topic Destinations

Perhaps the most common destination type used by STOMP clients is `/topic/<name>`. They perform topic matching on
publishing messages against subscriber patterns and can route a message to multiple subscribers (each gets its own copy)
.

Messages sent to a topic destination that has no active subscribers are simply discarded.

---

## More Info

Spring Framework WebSockets Documentation

- https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#websocket
- https://spring.io/guides/gs/messaging-stomp-websocket/

RabbitMQ STOMP Plugin

- https://www.rabbitmq.com/stomp.html

