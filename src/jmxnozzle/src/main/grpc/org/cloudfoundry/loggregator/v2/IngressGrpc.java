package org.cloudfoundry.loggregator.v2;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.9.0)",
    comments = "Source: ingress.proto")
public final class IngressGrpc {

  private IngressGrpc() {}

  public static final String SERVICE_NAME = "loggregator.v2.Ingress";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getSenderMethod()} instead. 
  public static final io.grpc.MethodDescriptor<org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.Envelope,
      org.cloudfoundry.loggregator.v2.LoggregatorIngress.IngressResponse> METHOD_SENDER = getSenderMethod();

  private static volatile io.grpc.MethodDescriptor<org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.Envelope,
      org.cloudfoundry.loggregator.v2.LoggregatorIngress.IngressResponse> getSenderMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.Envelope,
      org.cloudfoundry.loggregator.v2.LoggregatorIngress.IngressResponse> getSenderMethod() {
    io.grpc.MethodDescriptor<org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.Envelope, org.cloudfoundry.loggregator.v2.LoggregatorIngress.IngressResponse> getSenderMethod;
    if ((getSenderMethod = IngressGrpc.getSenderMethod) == null) {
      synchronized (IngressGrpc.class) {
        if ((getSenderMethod = IngressGrpc.getSenderMethod) == null) {
          IngressGrpc.getSenderMethod = getSenderMethod = 
              io.grpc.MethodDescriptor.<org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.Envelope, org.cloudfoundry.loggregator.v2.LoggregatorIngress.IngressResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
              .setFullMethodName(generateFullMethodName(
                  "loggregator.v2.Ingress", "Sender"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.Envelope.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.cloudfoundry.loggregator.v2.LoggregatorIngress.IngressResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new IngressMethodDescriptorSupplier("Sender"))
                  .build();
          }
        }
     }
     return getSenderMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getBatchSenderMethod()} instead. 
  public static final io.grpc.MethodDescriptor<org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.EnvelopeBatch,
      org.cloudfoundry.loggregator.v2.LoggregatorIngress.BatchSenderResponse> METHOD_BATCH_SENDER = getBatchSenderMethod();

  private static volatile io.grpc.MethodDescriptor<org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.EnvelopeBatch,
      org.cloudfoundry.loggregator.v2.LoggregatorIngress.BatchSenderResponse> getBatchSenderMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.EnvelopeBatch,
      org.cloudfoundry.loggregator.v2.LoggregatorIngress.BatchSenderResponse> getBatchSenderMethod() {
    io.grpc.MethodDescriptor<org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.EnvelopeBatch, org.cloudfoundry.loggregator.v2.LoggregatorIngress.BatchSenderResponse> getBatchSenderMethod;
    if ((getBatchSenderMethod = IngressGrpc.getBatchSenderMethod) == null) {
      synchronized (IngressGrpc.class) {
        if ((getBatchSenderMethod = IngressGrpc.getBatchSenderMethod) == null) {
          IngressGrpc.getBatchSenderMethod = getBatchSenderMethod = 
              io.grpc.MethodDescriptor.<org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.EnvelopeBatch, org.cloudfoundry.loggregator.v2.LoggregatorIngress.BatchSenderResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
              .setFullMethodName(generateFullMethodName(
                  "loggregator.v2.Ingress", "BatchSender"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.EnvelopeBatch.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.cloudfoundry.loggregator.v2.LoggregatorIngress.BatchSenderResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new IngressMethodDescriptorSupplier("BatchSender"))
                  .build();
          }
        }
     }
     return getBatchSenderMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getSendMethod()} instead. 
  public static final io.grpc.MethodDescriptor<org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.EnvelopeBatch,
      org.cloudfoundry.loggregator.v2.LoggregatorIngress.SendResponse> METHOD_SEND = getSendMethod();

  private static volatile io.grpc.MethodDescriptor<org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.EnvelopeBatch,
      org.cloudfoundry.loggregator.v2.LoggregatorIngress.SendResponse> getSendMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.EnvelopeBatch,
      org.cloudfoundry.loggregator.v2.LoggregatorIngress.SendResponse> getSendMethod() {
    io.grpc.MethodDescriptor<org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.EnvelopeBatch, org.cloudfoundry.loggregator.v2.LoggregatorIngress.SendResponse> getSendMethod;
    if ((getSendMethod = IngressGrpc.getSendMethod) == null) {
      synchronized (IngressGrpc.class) {
        if ((getSendMethod = IngressGrpc.getSendMethod) == null) {
          IngressGrpc.getSendMethod = getSendMethod = 
              io.grpc.MethodDescriptor.<org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.EnvelopeBatch, org.cloudfoundry.loggregator.v2.LoggregatorIngress.SendResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "loggregator.v2.Ingress", "Send"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.EnvelopeBatch.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.cloudfoundry.loggregator.v2.LoggregatorIngress.SendResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new IngressMethodDescriptorSupplier("Send"))
                  .build();
          }
        }
     }
     return getSendMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static IngressStub newStub(io.grpc.Channel channel) {
    return new IngressStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static IngressBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new IngressBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static IngressFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new IngressFutureStub(channel);
  }

  /**
   */
  public static abstract class IngressImplBase implements io.grpc.BindableService {

    /**
     */
    public io.grpc.stub.StreamObserver<org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.Envelope> sender(
        io.grpc.stub.StreamObserver<org.cloudfoundry.loggregator.v2.LoggregatorIngress.IngressResponse> responseObserver) {
      return asyncUnimplementedStreamingCall(getSenderMethod(), responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.EnvelopeBatch> batchSender(
        io.grpc.stub.StreamObserver<org.cloudfoundry.loggregator.v2.LoggregatorIngress.BatchSenderResponse> responseObserver) {
      return asyncUnimplementedStreamingCall(getBatchSenderMethod(), responseObserver);
    }

    /**
     */
    public void send(org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.EnvelopeBatch request,
        io.grpc.stub.StreamObserver<org.cloudfoundry.loggregator.v2.LoggregatorIngress.SendResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getSendMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getSenderMethod(),
            asyncClientStreamingCall(
              new MethodHandlers<
                org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.Envelope,
                org.cloudfoundry.loggregator.v2.LoggregatorIngress.IngressResponse>(
                  this, METHODID_SENDER)))
          .addMethod(
            getBatchSenderMethod(),
            asyncClientStreamingCall(
              new MethodHandlers<
                org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.EnvelopeBatch,
                org.cloudfoundry.loggregator.v2.LoggregatorIngress.BatchSenderResponse>(
                  this, METHODID_BATCH_SENDER)))
          .addMethod(
            getSendMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.EnvelopeBatch,
                org.cloudfoundry.loggregator.v2.LoggregatorIngress.SendResponse>(
                  this, METHODID_SEND)))
          .build();
    }
  }

  /**
   */
  public static final class IngressStub extends io.grpc.stub.AbstractStub<IngressStub> {
    private IngressStub(io.grpc.Channel channel) {
      super(channel);
    }

    private IngressStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected IngressStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new IngressStub(channel, callOptions);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.Envelope> sender(
        io.grpc.stub.StreamObserver<org.cloudfoundry.loggregator.v2.LoggregatorIngress.IngressResponse> responseObserver) {
      return asyncClientStreamingCall(
          getChannel().newCall(getSenderMethod(), getCallOptions()), responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.EnvelopeBatch> batchSender(
        io.grpc.stub.StreamObserver<org.cloudfoundry.loggregator.v2.LoggregatorIngress.BatchSenderResponse> responseObserver) {
      return asyncClientStreamingCall(
          getChannel().newCall(getBatchSenderMethod(), getCallOptions()), responseObserver);
    }

    /**
     */
    public void send(org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.EnvelopeBatch request,
        io.grpc.stub.StreamObserver<org.cloudfoundry.loggregator.v2.LoggregatorIngress.SendResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getSendMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class IngressBlockingStub extends io.grpc.stub.AbstractStub<IngressBlockingStub> {
    private IngressBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private IngressBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected IngressBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new IngressBlockingStub(channel, callOptions);
    }

    /**
     */
    public org.cloudfoundry.loggregator.v2.LoggregatorIngress.SendResponse send(org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.EnvelopeBatch request) {
      return blockingUnaryCall(
          getChannel(), getSendMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class IngressFutureStub extends io.grpc.stub.AbstractStub<IngressFutureStub> {
    private IngressFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private IngressFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected IngressFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new IngressFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.cloudfoundry.loggregator.v2.LoggregatorIngress.SendResponse> send(
        org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.EnvelopeBatch request) {
      return futureUnaryCall(
          getChannel().newCall(getSendMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_SEND = 0;
  private static final int METHODID_SENDER = 1;
  private static final int METHODID_BATCH_SENDER = 2;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final IngressImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(IngressImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_SEND:
          serviceImpl.send((org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.EnvelopeBatch) request,
              (io.grpc.stub.StreamObserver<org.cloudfoundry.loggregator.v2.LoggregatorIngress.SendResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_SENDER:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.sender(
              (io.grpc.stub.StreamObserver<org.cloudfoundry.loggregator.v2.LoggregatorIngress.IngressResponse>) responseObserver);
        case METHODID_BATCH_SENDER:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.batchSender(
              (io.grpc.stub.StreamObserver<org.cloudfoundry.loggregator.v2.LoggregatorIngress.BatchSenderResponse>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class IngressBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    IngressBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return org.cloudfoundry.loggregator.v2.LoggregatorIngress.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Ingress");
    }
  }

  private static final class IngressFileDescriptorSupplier
      extends IngressBaseDescriptorSupplier {
    IngressFileDescriptorSupplier() {}
  }

  private static final class IngressMethodDescriptorSupplier
      extends IngressBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    IngressMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (IngressGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new IngressFileDescriptorSupplier())
              .addMethod(getSenderMethod())
              .addMethod(getBatchSenderMethod())
              .addMethod(getSendMethod())
              .build();
        }
      }
    }
    return result;
  }
}
