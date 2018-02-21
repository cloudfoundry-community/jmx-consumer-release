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
    comments = "Source: egress.proto")
public final class EgressGrpc {

  private EgressGrpc() {}

  public static final String SERVICE_NAME = "loggregator.v2.Egress";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getReceiverMethod()} instead. 
  public static final io.grpc.MethodDescriptor<org.cloudfoundry.loggregator.v2.LoggregatorEgress.EgressRequest,
      org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.Envelope> METHOD_RECEIVER = getReceiverMethod();

  private static volatile io.grpc.MethodDescriptor<org.cloudfoundry.loggregator.v2.LoggregatorEgress.EgressRequest,
      org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.Envelope> getReceiverMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<org.cloudfoundry.loggregator.v2.LoggregatorEgress.EgressRequest,
      org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.Envelope> getReceiverMethod() {
    io.grpc.MethodDescriptor<org.cloudfoundry.loggregator.v2.LoggregatorEgress.EgressRequest, org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.Envelope> getReceiverMethod;
    if ((getReceiverMethod = EgressGrpc.getReceiverMethod) == null) {
      synchronized (EgressGrpc.class) {
        if ((getReceiverMethod = EgressGrpc.getReceiverMethod) == null) {
          EgressGrpc.getReceiverMethod = getReceiverMethod = 
              io.grpc.MethodDescriptor.<org.cloudfoundry.loggregator.v2.LoggregatorEgress.EgressRequest, org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.Envelope>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(
                  "loggregator.v2.Egress", "Receiver"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.cloudfoundry.loggregator.v2.LoggregatorEgress.EgressRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.Envelope.getDefaultInstance()))
                  .setSchemaDescriptor(new EgressMethodDescriptorSupplier("Receiver"))
                  .build();
          }
        }
     }
     return getReceiverMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getBatchedReceiverMethod()} instead. 
  public static final io.grpc.MethodDescriptor<org.cloudfoundry.loggregator.v2.LoggregatorEgress.EgressBatchRequest,
      org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.EnvelopeBatch> METHOD_BATCHED_RECEIVER = getBatchedReceiverMethod();

  private static volatile io.grpc.MethodDescriptor<org.cloudfoundry.loggregator.v2.LoggregatorEgress.EgressBatchRequest,
      org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.EnvelopeBatch> getBatchedReceiverMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<org.cloudfoundry.loggregator.v2.LoggregatorEgress.EgressBatchRequest,
      org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.EnvelopeBatch> getBatchedReceiverMethod() {
    io.grpc.MethodDescriptor<org.cloudfoundry.loggregator.v2.LoggregatorEgress.EgressBatchRequest, org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.EnvelopeBatch> getBatchedReceiverMethod;
    if ((getBatchedReceiverMethod = EgressGrpc.getBatchedReceiverMethod) == null) {
      synchronized (EgressGrpc.class) {
        if ((getBatchedReceiverMethod = EgressGrpc.getBatchedReceiverMethod) == null) {
          EgressGrpc.getBatchedReceiverMethod = getBatchedReceiverMethod = 
              io.grpc.MethodDescriptor.<org.cloudfoundry.loggregator.v2.LoggregatorEgress.EgressBatchRequest, org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.EnvelopeBatch>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(
                  "loggregator.v2.Egress", "BatchedReceiver"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.cloudfoundry.loggregator.v2.LoggregatorEgress.EgressBatchRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.EnvelopeBatch.getDefaultInstance()))
                  .setSchemaDescriptor(new EgressMethodDescriptorSupplier("BatchedReceiver"))
                  .build();
          }
        }
     }
     return getBatchedReceiverMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static EgressStub newStub(io.grpc.Channel channel) {
    return new EgressStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static EgressBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new EgressBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static EgressFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new EgressFutureStub(channel);
  }

  /**
   */
  public static abstract class EgressImplBase implements io.grpc.BindableService {

    /**
     */
    public void receiver(org.cloudfoundry.loggregator.v2.LoggregatorEgress.EgressRequest request,
        io.grpc.stub.StreamObserver<org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.Envelope> responseObserver) {
      asyncUnimplementedUnaryCall(getReceiverMethod(), responseObserver);
    }

    /**
     */
    public void batchedReceiver(org.cloudfoundry.loggregator.v2.LoggregatorEgress.EgressBatchRequest request,
        io.grpc.stub.StreamObserver<org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.EnvelopeBatch> responseObserver) {
      asyncUnimplementedUnaryCall(getBatchedReceiverMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getReceiverMethod(),
            asyncServerStreamingCall(
              new MethodHandlers<
                org.cloudfoundry.loggregator.v2.LoggregatorEgress.EgressRequest,
                org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.Envelope>(
                  this, METHODID_RECEIVER)))
          .addMethod(
            getBatchedReceiverMethod(),
            asyncServerStreamingCall(
              new MethodHandlers<
                org.cloudfoundry.loggregator.v2.LoggregatorEgress.EgressBatchRequest,
                org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.EnvelopeBatch>(
                  this, METHODID_BATCHED_RECEIVER)))
          .build();
    }
  }

  /**
   */
  public static final class EgressStub extends io.grpc.stub.AbstractStub<EgressStub> {
    private EgressStub(io.grpc.Channel channel) {
      super(channel);
    }

    private EgressStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected EgressStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new EgressStub(channel, callOptions);
    }

    /**
     */
    public void receiver(org.cloudfoundry.loggregator.v2.LoggregatorEgress.EgressRequest request,
        io.grpc.stub.StreamObserver<org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.Envelope> responseObserver) {
      asyncServerStreamingCall(
          getChannel().newCall(getReceiverMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void batchedReceiver(org.cloudfoundry.loggregator.v2.LoggregatorEgress.EgressBatchRequest request,
        io.grpc.stub.StreamObserver<org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.EnvelopeBatch> responseObserver) {
      asyncServerStreamingCall(
          getChannel().newCall(getBatchedReceiverMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class EgressBlockingStub extends io.grpc.stub.AbstractStub<EgressBlockingStub> {
    private EgressBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private EgressBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected EgressBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new EgressBlockingStub(channel, callOptions);
    }

    /**
     */
    public java.util.Iterator<org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.Envelope> receiver(
        org.cloudfoundry.loggregator.v2.LoggregatorEgress.EgressRequest request) {
      return blockingServerStreamingCall(
          getChannel(), getReceiverMethod(), getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.EnvelopeBatch> batchedReceiver(
        org.cloudfoundry.loggregator.v2.LoggregatorEgress.EgressBatchRequest request) {
      return blockingServerStreamingCall(
          getChannel(), getBatchedReceiverMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class EgressFutureStub extends io.grpc.stub.AbstractStub<EgressFutureStub> {
    private EgressFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private EgressFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected EgressFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new EgressFutureStub(channel, callOptions);
    }
  }

  private static final int METHODID_RECEIVER = 0;
  private static final int METHODID_BATCHED_RECEIVER = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final EgressImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(EgressImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_RECEIVER:
          serviceImpl.receiver((org.cloudfoundry.loggregator.v2.LoggregatorEgress.EgressRequest) request,
              (io.grpc.stub.StreamObserver<org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.Envelope>) responseObserver);
          break;
        case METHODID_BATCHED_RECEIVER:
          serviceImpl.batchedReceiver((org.cloudfoundry.loggregator.v2.LoggregatorEgress.EgressBatchRequest) request,
              (io.grpc.stub.StreamObserver<org.cloudfoundry.loggregator.v2.LoggregatorEnvelope.EnvelopeBatch>) responseObserver);
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
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class EgressBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    EgressBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return org.cloudfoundry.loggregator.v2.LoggregatorEgress.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Egress");
    }
  }

  private static final class EgressFileDescriptorSupplier
      extends EgressBaseDescriptorSupplier {
    EgressFileDescriptorSupplier() {}
  }

  private static final class EgressMethodDescriptorSupplier
      extends EgressBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    EgressMethodDescriptorSupplier(String methodName) {
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
      synchronized (EgressGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new EgressFileDescriptorSupplier())
              .addMethod(getReceiverMethod())
              .addMethod(getBatchedReceiverMethod())
              .build();
        }
      }
    }
    return result;
  }
}
