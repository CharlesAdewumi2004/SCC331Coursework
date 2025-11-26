package frontend;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.63.0)",
    comments = "Source: auction.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class AuctionServiceGrpc {

  private AuctionServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "AuctionService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<frontend.RegisterRequest,
      frontend.RegisterReply> getRegisterMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Register",
      requestType = frontend.RegisterRequest.class,
      responseType = frontend.RegisterReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<frontend.RegisterRequest,
      frontend.RegisterReply> getRegisterMethod() {
    io.grpc.MethodDescriptor<frontend.RegisterRequest, frontend.RegisterReply> getRegisterMethod;
    if ((getRegisterMethod = AuctionServiceGrpc.getRegisterMethod) == null) {
      synchronized (AuctionServiceGrpc.class) {
        if ((getRegisterMethod = AuctionServiceGrpc.getRegisterMethod) == null) {
          AuctionServiceGrpc.getRegisterMethod = getRegisterMethod =
              io.grpc.MethodDescriptor.<frontend.RegisterRequest, frontend.RegisterReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Register"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  frontend.RegisterRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  frontend.RegisterReply.getDefaultInstance()))
              .setSchemaDescriptor(new AuctionServiceMethodDescriptorSupplier("Register"))
              .build();
        }
      }
    }
    return getRegisterMethod;
  }

  private static volatile io.grpc.MethodDescriptor<frontend.NewAuctionRequest,
      frontend.NewAuctionReply> getNewAuctionMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "NewAuction",
      requestType = frontend.NewAuctionRequest.class,
      responseType = frontend.NewAuctionReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<frontend.NewAuctionRequest,
      frontend.NewAuctionReply> getNewAuctionMethod() {
    io.grpc.MethodDescriptor<frontend.NewAuctionRequest, frontend.NewAuctionReply> getNewAuctionMethod;
    if ((getNewAuctionMethod = AuctionServiceGrpc.getNewAuctionMethod) == null) {
      synchronized (AuctionServiceGrpc.class) {
        if ((getNewAuctionMethod = AuctionServiceGrpc.getNewAuctionMethod) == null) {
          AuctionServiceGrpc.getNewAuctionMethod = getNewAuctionMethod =
              io.grpc.MethodDescriptor.<frontend.NewAuctionRequest, frontend.NewAuctionReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "NewAuction"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  frontend.NewAuctionRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  frontend.NewAuctionReply.getDefaultInstance()))
              .setSchemaDescriptor(new AuctionServiceMethodDescriptorSupplier("NewAuction"))
              .build();
        }
      }
    }
    return getNewAuctionMethod;
  }

  private static volatile io.grpc.MethodDescriptor<frontend.BidRequest,
      frontend.BidReply> getBidMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Bid",
      requestType = frontend.BidRequest.class,
      responseType = frontend.BidReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<frontend.BidRequest,
      frontend.BidReply> getBidMethod() {
    io.grpc.MethodDescriptor<frontend.BidRequest, frontend.BidReply> getBidMethod;
    if ((getBidMethod = AuctionServiceGrpc.getBidMethod) == null) {
      synchronized (AuctionServiceGrpc.class) {
        if ((getBidMethod = AuctionServiceGrpc.getBidMethod) == null) {
          AuctionServiceGrpc.getBidMethod = getBidMethod =
              io.grpc.MethodDescriptor.<frontend.BidRequest, frontend.BidReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Bid"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  frontend.BidRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  frontend.BidReply.getDefaultInstance()))
              .setSchemaDescriptor(new AuctionServiceMethodDescriptorSupplier("Bid"))
              .build();
        }
      }
    }
    return getBidMethod;
  }

  private static volatile io.grpc.MethodDescriptor<frontend.Empty,
      frontend.ListReply> getListItemsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ListItems",
      requestType = frontend.Empty.class,
      responseType = frontend.ListReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<frontend.Empty,
      frontend.ListReply> getListItemsMethod() {
    io.grpc.MethodDescriptor<frontend.Empty, frontend.ListReply> getListItemsMethod;
    if ((getListItemsMethod = AuctionServiceGrpc.getListItemsMethod) == null) {
      synchronized (AuctionServiceGrpc.class) {
        if ((getListItemsMethod = AuctionServiceGrpc.getListItemsMethod) == null) {
          AuctionServiceGrpc.getListItemsMethod = getListItemsMethod =
              io.grpc.MethodDescriptor.<frontend.Empty, frontend.ListReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ListItems"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  frontend.Empty.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  frontend.ListReply.getDefaultInstance()))
              .setSchemaDescriptor(new AuctionServiceMethodDescriptorSupplier("ListItems"))
              .build();
        }
      }
    }
    return getListItemsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<frontend.GetSpecRequest,
      frontend.Item> getGetSpecMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetSpec",
      requestType = frontend.GetSpecRequest.class,
      responseType = frontend.Item.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<frontend.GetSpecRequest,
      frontend.Item> getGetSpecMethod() {
    io.grpc.MethodDescriptor<frontend.GetSpecRequest, frontend.Item> getGetSpecMethod;
    if ((getGetSpecMethod = AuctionServiceGrpc.getGetSpecMethod) == null) {
      synchronized (AuctionServiceGrpc.class) {
        if ((getGetSpecMethod = AuctionServiceGrpc.getGetSpecMethod) == null) {
          AuctionServiceGrpc.getGetSpecMethod = getGetSpecMethod =
              io.grpc.MethodDescriptor.<frontend.GetSpecRequest, frontend.Item>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetSpec"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  frontend.GetSpecRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  frontend.Item.getDefaultInstance()))
              .setSchemaDescriptor(new AuctionServiceMethodDescriptorSupplier("GetSpec"))
              .build();
        }
      }
    }
    return getGetSpecMethod;
  }

  private static volatile io.grpc.MethodDescriptor<frontend.CloseRequest,
      frontend.AuctionResult> getCloseAuctionMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CloseAuction",
      requestType = frontend.CloseRequest.class,
      responseType = frontend.AuctionResult.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<frontend.CloseRequest,
      frontend.AuctionResult> getCloseAuctionMethod() {
    io.grpc.MethodDescriptor<frontend.CloseRequest, frontend.AuctionResult> getCloseAuctionMethod;
    if ((getCloseAuctionMethod = AuctionServiceGrpc.getCloseAuctionMethod) == null) {
      synchronized (AuctionServiceGrpc.class) {
        if ((getCloseAuctionMethod = AuctionServiceGrpc.getCloseAuctionMethod) == null) {
          AuctionServiceGrpc.getCloseAuctionMethod = getCloseAuctionMethod =
              io.grpc.MethodDescriptor.<frontend.CloseRequest, frontend.AuctionResult>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CloseAuction"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  frontend.CloseRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  frontend.AuctionResult.getDefaultInstance()))
              .setSchemaDescriptor(new AuctionServiceMethodDescriptorSupplier("CloseAuction"))
              .build();
        }
      }
    }
    return getCloseAuctionMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static AuctionServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<AuctionServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<AuctionServiceStub>() {
        @java.lang.Override
        public AuctionServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new AuctionServiceStub(channel, callOptions);
        }
      };
    return AuctionServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static AuctionServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<AuctionServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<AuctionServiceBlockingStub>() {
        @java.lang.Override
        public AuctionServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new AuctionServiceBlockingStub(channel, callOptions);
        }
      };
    return AuctionServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static AuctionServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<AuctionServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<AuctionServiceFutureStub>() {
        @java.lang.Override
        public AuctionServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new AuctionServiceFutureStub(channel, callOptions);
        }
      };
    return AuctionServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void register(frontend.RegisterRequest request,
        io.grpc.stub.StreamObserver<frontend.RegisterReply> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getRegisterMethod(), responseObserver);
    }

    /**
     */
    default void newAuction(frontend.NewAuctionRequest request,
        io.grpc.stub.StreamObserver<frontend.NewAuctionReply> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getNewAuctionMethod(), responseObserver);
    }

    /**
     */
    default void bid(frontend.BidRequest request,
        io.grpc.stub.StreamObserver<frontend.BidReply> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getBidMethod(), responseObserver);
    }

    /**
     */
    default void listItems(frontend.Empty request,
        io.grpc.stub.StreamObserver<frontend.ListReply> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getListItemsMethod(), responseObserver);
    }

    /**
     */
    default void getSpec(frontend.GetSpecRequest request,
        io.grpc.stub.StreamObserver<frontend.Item> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetSpecMethod(), responseObserver);
    }

    /**
     */
    default void closeAuction(frontend.CloseRequest request,
        io.grpc.stub.StreamObserver<frontend.AuctionResult> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCloseAuctionMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service AuctionService.
   */
  public static abstract class AuctionServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return AuctionServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service AuctionService.
   */
  public static final class AuctionServiceStub
      extends io.grpc.stub.AbstractAsyncStub<AuctionServiceStub> {
    private AuctionServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AuctionServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new AuctionServiceStub(channel, callOptions);
    }

    /**
     */
    public void register(frontend.RegisterRequest request,
        io.grpc.stub.StreamObserver<frontend.RegisterReply> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getRegisterMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void newAuction(frontend.NewAuctionRequest request,
        io.grpc.stub.StreamObserver<frontend.NewAuctionReply> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getNewAuctionMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void bid(frontend.BidRequest request,
        io.grpc.stub.StreamObserver<frontend.BidReply> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getBidMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void listItems(frontend.Empty request,
        io.grpc.stub.StreamObserver<frontend.ListReply> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getListItemsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getSpec(frontend.GetSpecRequest request,
        io.grpc.stub.StreamObserver<frontend.Item> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetSpecMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void closeAuction(frontend.CloseRequest request,
        io.grpc.stub.StreamObserver<frontend.AuctionResult> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCloseAuctionMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service AuctionService.
   */
  public static final class AuctionServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<AuctionServiceBlockingStub> {
    private AuctionServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AuctionServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new AuctionServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public frontend.RegisterReply register(frontend.RegisterRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getRegisterMethod(), getCallOptions(), request);
    }

    /**
     */
    public frontend.NewAuctionReply newAuction(frontend.NewAuctionRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getNewAuctionMethod(), getCallOptions(), request);
    }

    /**
     */
    public frontend.BidReply bid(frontend.BidRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getBidMethod(), getCallOptions(), request);
    }

    /**
     */
    public frontend.ListReply listItems(frontend.Empty request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getListItemsMethod(), getCallOptions(), request);
    }

    /**
     */
    public frontend.Item getSpec(frontend.GetSpecRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetSpecMethod(), getCallOptions(), request);
    }

    /**
     */
    public frontend.AuctionResult closeAuction(frontend.CloseRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCloseAuctionMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service AuctionService.
   */
  public static final class AuctionServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<AuctionServiceFutureStub> {
    private AuctionServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AuctionServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new AuctionServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<frontend.RegisterReply> register(
        frontend.RegisterRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getRegisterMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<frontend.NewAuctionReply> newAuction(
        frontend.NewAuctionRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getNewAuctionMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<frontend.BidReply> bid(
        frontend.BidRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getBidMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<frontend.ListReply> listItems(
        frontend.Empty request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getListItemsMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<frontend.Item> getSpec(
        frontend.GetSpecRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetSpecMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<frontend.AuctionResult> closeAuction(
        frontend.CloseRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCloseAuctionMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_REGISTER = 0;
  private static final int METHODID_NEW_AUCTION = 1;
  private static final int METHODID_BID = 2;
  private static final int METHODID_LIST_ITEMS = 3;
  private static final int METHODID_GET_SPEC = 4;
  private static final int METHODID_CLOSE_AUCTION = 5;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_REGISTER:
          serviceImpl.register((frontend.RegisterRequest) request,
              (io.grpc.stub.StreamObserver<frontend.RegisterReply>) responseObserver);
          break;
        case METHODID_NEW_AUCTION:
          serviceImpl.newAuction((frontend.NewAuctionRequest) request,
              (io.grpc.stub.StreamObserver<frontend.NewAuctionReply>) responseObserver);
          break;
        case METHODID_BID:
          serviceImpl.bid((frontend.BidRequest) request,
              (io.grpc.stub.StreamObserver<frontend.BidReply>) responseObserver);
          break;
        case METHODID_LIST_ITEMS:
          serviceImpl.listItems((frontend.Empty) request,
              (io.grpc.stub.StreamObserver<frontend.ListReply>) responseObserver);
          break;
        case METHODID_GET_SPEC:
          serviceImpl.getSpec((frontend.GetSpecRequest) request,
              (io.grpc.stub.StreamObserver<frontend.Item>) responseObserver);
          break;
        case METHODID_CLOSE_AUCTION:
          serviceImpl.closeAuction((frontend.CloseRequest) request,
              (io.grpc.stub.StreamObserver<frontend.AuctionResult>) responseObserver);
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

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getRegisterMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              frontend.RegisterRequest,
              frontend.RegisterReply>(
                service, METHODID_REGISTER)))
        .addMethod(
          getNewAuctionMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              frontend.NewAuctionRequest,
              frontend.NewAuctionReply>(
                service, METHODID_NEW_AUCTION)))
        .addMethod(
          getBidMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              frontend.BidRequest,
              frontend.BidReply>(
                service, METHODID_BID)))
        .addMethod(
          getListItemsMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              frontend.Empty,
              frontend.ListReply>(
                service, METHODID_LIST_ITEMS)))
        .addMethod(
          getGetSpecMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              frontend.GetSpecRequest,
              frontend.Item>(
                service, METHODID_GET_SPEC)))
        .addMethod(
          getCloseAuctionMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              frontend.CloseRequest,
              frontend.AuctionResult>(
                service, METHODID_CLOSE_AUCTION)))
        .build();
  }

  private static abstract class AuctionServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    AuctionServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return frontend.AuctionProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("AuctionService");
    }
  }

  private static final class AuctionServiceFileDescriptorSupplier
      extends AuctionServiceBaseDescriptorSupplier {
    AuctionServiceFileDescriptorSupplier() {}
  }

  private static final class AuctionServiceMethodDescriptorSupplier
      extends AuctionServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    AuctionServiceMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (AuctionServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new AuctionServiceFileDescriptorSupplier())
              .addMethod(getRegisterMethod())
              .addMethod(getNewAuctionMethod())
              .addMethod(getBidMethod())
              .addMethod(getListItemsMethod())
              .addMethod(getGetSpecMethod())
              .addMethod(getCloseAuctionMethod())
              .build();
        }
      }
    }
    return result;
  }
}
