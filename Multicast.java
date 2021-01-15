
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Multicast implements Runnable {

	// 多点广播组IP地址
	private static final String MULTICAST_IP = "230.0.0.3";
	// 多点广播组端口
	private static final int MULTICAST_PORT = 9900;
	// 定义每个数据报的最大为4K
	private static final int MAX_LEN = 4096;
	
	// 定义MulticastSocket实例
	private MulticastSocket socket = null;
	// 多点广播组IP对象
	private InetAddress multicastAddress = null;
	
	private Scanner scan = null;
	
	// 定义接收网络数据的字节数组
	byte[] inBuff = new byte[MAX_LEN];
	
	// 以指定字节数组创建准备接受数据的DatagramPacket对象
	private DatagramPacket receivePacket = new DatagramPacket(inBuff, inBuff.length);
	
	// 定义一个用于发送的DatagramPacket对象
	private DatagramPacket senderPacket = null;
	
	private SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
	
	public Multicast() {
		this.Init();
	}
	
	private void Init() {
		try {
			multicastAddress = InetAddress.getByName(MULTICAST_IP);
			
			// 创建用于发送、接收数据的MulticastSocket对象
			// 因为该MulticastSocket对象需要接收，所以有指定端口
			socket = new MulticastSocket(MULTICAST_PORT);
			// 加入指定的多点广播地址
			socket.joinGroup(multicastAddress);
			
			// 设置是否接收本机发送的消息，true：不接收，false：接收
			socket.setLoopbackMode(true);
			
			// 初始化发送的DatagramSocket，它包含一个长度为0的字节数组
			senderPacket = new DatagramPacket(new byte[0], 0, multicastAddress, MULTICAST_PORT);
			
			// 启动定时任务，间隔50秒发送空消息，以保持连接
			this.timerTask(50);
			
			// 启动以本实例的run()方法作为线程体的线程
			new Thread(this).start();
			
			// 创建键盘输入流
			scan = new Scanner(System.in);
			// 不断读取键盘输入
			while (scan.hasNextLine()) {
				Send(scan.nextLine());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			socket.close();
		}
	}
	
	public void Send(String msg) {
		try {
			senderPacket.setData(msg.getBytes());
			socket.send(senderPacket);
			System.out.println( "[" + format.format(new Date()) + "] sender: "+msg);
		}catch (Exception e) {
		}
	}
	
	@Override
	public void run() {
		try {
			while (true) {
				// 读取Socket中的数据，读到的数据放在inPacket所封装的字节数组里。
				socket.receive(receivePacket);
				// 打印输出从socket中读取的内容
				System.out.println("[" + format.format(new Date())+"] Receive：" + new String(inBuff, 0, receivePacket.getLength()));
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			try {
				if (socket != null) {
					// 让该Socket离开该多点IP广播地址
					socket.leaveGroup(multicastAddress);
					// 关闭该Socket对象
					socket.close();
				}
				System.exit(1);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void timerTask(int seconds) {
		LifeRunnable task = new LifeRunnable(this);
		ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
		timer.scheduleAtFixedRate(task, 5, seconds, TimeUnit.SECONDS);
	}
	
	class LifeRunnable implements Runnable{
		Multicast _main = null;
		String ip = "";
		LifeRunnable(Multicast main){
			this._main = main;
			try {
				InetAddress addr = InetAddress.getLocalHost();
				ip = addr.getHostAddress();
			}catch (Exception e) {
			}
		}
		@Override
		public void run() {
			this._main.Send("alive|"+ip);
		}
	}

	public static void main(String[] args) {
		Multicast m = new Multicast();
	}
}
