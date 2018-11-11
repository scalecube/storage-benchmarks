package io.scalecube.storages.common.entity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.hash.serialization.BytesReader;
import net.openhft.chronicle.hash.serialization.BytesWriter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Order implements Externalizable {

  private static final Random RANDOM = new Random(42);

  private UUID id;
  private String userId;
  private String instrumentInstanceId;
  private String instrumentName;
  private BigDecimal quantity;
  private BigDecimal remainingQuantity;
  private OrderType orderType;
  private OrderSide side;
  private BigDecimal price;
  private LocalDateTime clientTimestamp;
  private LocalDateTime serverTimestamp;
  private String userIpAddress;
  private OrderStatus status;
  private List<Fill> fills;

  private Order() {
  }

  /**
   * Generates a random order by the given order identifier.
   *
   * @param orderId order identifier
   */
  public Order(UUID orderId) {
    this.id = orderId;
    userId = "01234567-8901-2345-6789-012345678901";
    instrumentInstanceId = "01234567-8901-2345-6789-012345678901";
    instrumentName = "BTC";
    quantity = BigDecimal.valueOf(RANDOM.nextLong());
    remainingQuantity = BigDecimal.valueOf(RANDOM.nextLong());
    orderType = RANDOM.nextBoolean() ? OrderType.Limit : OrderType.Market;
    side = RANDOM.nextBoolean() ? OrderSide.Buy : OrderSide.Sell;
    price = BigDecimal.valueOf(RANDOM.nextLong());
    clientTimestamp = LocalDateTime.now();
    serverTimestamp = LocalDateTime.now();
    userIpAddress = "127.100.101.196";
    status = OrderStatus.ALL[RANDOM.nextInt(OrderStatus.ALL.length)];
    fills = Arrays.asList(
        new Fill(BigDecimal.valueOf(RANDOM.nextLong()), BigDecimal.valueOf(RANDOM.nextLong()),
            System.currentTimeMillis()),
        new Fill(BigDecimal.valueOf(RANDOM.nextLong()), BigDecimal.valueOf(RANDOM.nextLong()),
            System.currentTimeMillis()));
  }

  public UUID id() {
    return id;
  }

  /**
   * Creates a new instance of this order with new order status.
   *
   * @param status order status
   * @return an order
   */
  public Order withNewStatus(OrderStatus status) {
    Order order = clone();
    order.status = status;
    return order;
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeLong(id.getMostSignificantBits());
    out.writeLong(id.getLeastSignificantBits());
    out.writeUTF(userId);
    out.writeUTF(instrumentInstanceId);
    out.writeUTF(instrumentName != null ? instrumentName : "");
    out.writeByte(orderType.code);
    out.writeByte(side.code);
    BigDecimalUtil.writeObject(quantity, out);
    BigDecimalUtil.writeObject(remainingQuantity, out);
    BigDecimalUtil.writeObject(price, out);
    out.writeObject(clientTimestamp);
    out.writeObject(serverTimestamp);
    out.writeUTF(userIpAddress);
    out.writeByte(status.code);
    out.writeInt(fills.size());
    for (Fill fill : fills) {
      fill.writeExternal(out);
    }
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    id = new UUID(in.readLong(), in.readLong());
    userId = in.readUTF();
    instrumentInstanceId = in.readUTF();
    String value = in.readUTF();
    instrumentName = value.isEmpty() ? null : value;
    orderType = OrderType.valueOf(in.readByte());
    side = OrderSide.valueOf(in.readByte());
    quantity = BigDecimalUtil.readObject(in);
    remainingQuantity = BigDecimalUtil.readObject(in);
    price = BigDecimalUtil.readObject(in);
    clientTimestamp = (LocalDateTime) in.readObject();
    serverTimestamp = (LocalDateTime) in.readObject();
    userIpAddress = in.readUTF();
    status = OrderStatus.valueOf(in.readByte());
    int fillsLength = in.readInt();
    List<Fill> fills = new ArrayList<>(fillsLength);
    for (int i = 0; i < fillsLength; i++) {
      Fill fill = new Fill();
      fill.readExternal(in);
      fills.add(fill);
    }
    this.fills = fills;
  }

  /**
   * Converts this order to an array of bytes.
   *
   * @return array of bytes
   */
  public byte[] toBytes() throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(baos);
    writeExternal(oos);
    oos.flush();
    return baos.toByteArray();
  }

  /**
   * Converts the give array of bytes to an order.
   *
   * @param valBytes array of bytes
   * @return an order
   */
  public static Order fromBytes(byte[] valBytes) throws Exception {
    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(valBytes));
    Order order = new Order();
    order.readExternal(ois);
    return order;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Order order = (Order) o;
    return Objects.equals(id, order.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("Order{");
    sb.append("id=").append(id);
    sb.append(", userId='").append(userId).append('\'');
    sb.append(", instrumentInstanceId='").append(instrumentInstanceId).append('\'');
    sb.append(", instrumentName='").append(instrumentName).append('\'');
    sb.append(", quantity=").append(quantity);
    sb.append(", remainingQuantity=").append(remainingQuantity);
    sb.append(", orderType=").append(orderType);
    sb.append(", side=").append(side);
    sb.append(", price=").append(price);
    sb.append(", clientTimestamp=").append(clientTimestamp);
    sb.append(", serverTimestamp=").append(serverTimestamp);
    sb.append(", userIpAddress='").append(userIpAddress).append('\'');
    sb.append(", status=").append(status);
    sb.append(", fills=").append(fills);
    sb.append('}');
    return sb.toString();
  }

  @Override
  protected Order clone() {
    Order order = new Order();
    order.id = this.id;
    order.userId = this.userId;
    order.instrumentInstanceId = this.instrumentInstanceId;
    order.instrumentName = this.instrumentName;
    order.quantity = this.quantity;
    order.remainingQuantity = this.remainingQuantity;
    order.orderType = this.orderType;
    order.side = this.side;
    order.price = this.price;
    order.clientTimestamp = this.clientTimestamp;
    order.serverTimestamp = this.serverTimestamp;
    order.userIpAddress = this.userIpAddress;
    order.fills = this.fills;
    order.status = this.status;
    return order;
  }

  public static class Marshaller implements BytesReader<Order>, BytesWriter<Order> {

    @NotNull
    @Override
    public Order read(Bytes in, @Nullable Order using) {
      Order that = new Order();
      that.id = new UUID(in.readLong(), in.readLong());
      that.     userId = in.readUtf8();
      that.instrumentInstanceId = in.readUtf8();
      String value = in.readUtf8();
      that.      instrumentName = value == null || value.isEmpty() ? null : value;
      that.orderType = OrderType.valueOf(in.readByte());
      that.side = OrderSide.valueOf(in.readByte());
      that.quantity = BigDecimalUtil.readObject(in);
      that.remainingQuantity = BigDecimalUtil.readObject(in);
      that.price = BigDecimalUtil.readObject(in);
      that.userIpAddress = in.readUtf8();
      that.status = OrderStatus.valueOf(in.readByte());
      return that;
    }

    @Override
    public void write(Bytes out, @NotNull Order order) {
      out.writeLong(order.id.getMostSignificantBits());
      out.writeLong(order.id.getLeastSignificantBits());
      out.writeUtf8(order.userId);
      out.writeUtf8(order.instrumentInstanceId);
      out.writeUtf8(order.instrumentName != null ? order.instrumentName : "");
      out.writeByte((byte) order.orderType.code);
      out.writeByte((byte) order.side.code);
      BigDecimalUtil.writeObject(order.quantity, out);
      BigDecimalUtil.writeObject(order.remainingQuantity, out);
      BigDecimalUtil.writeObject(order.price, out);
      out.writeUtf8(order.userIpAddress);
      out.writeByte((byte) order.status.code);
    }
  }
}
