package xyz.chaobei.webtelnet.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.regex.Pattern;

@ServerEndpoint("/websocket/{sid}")
@Component
public class WebSocketServer {

    static Logger log= LoggerFactory.getLogger(WebSocketServer.class);
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;
    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<WebSocketServer>();

    private static StringBuilder cache = new StringBuilder();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    //接收sid
    private String sid="";

    private static final Pattern ansiRemovePattern = Pattern.compile("\\e[\\[\\(][0-9;]*[mGKFB]");

    private static final Pattern colorRemovePattern = Pattern.compile("(\\x9B|\\x1B\\[)[0-?]*[ -\\/]*[@-~]");
    /**
     * 连接建立成功调用的方法*/
    @OnOpen
    public void onOpen(Session session,@PathParam("sid") String sid) {
        this.session = session;
        webSocketSet.add(this);     //加入set中
        addOnlineCount();           //在线数加1
        log.info("有新窗口开始监听:"+sid+",当前在线人数为" + getOnlineCount());
        this.sid=sid;

    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);  //从set中删除
        subOnlineCount();           //在线数减1
        log.info("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息*/
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到来自窗口"+sid+"的信息:"+message);
        //群发消息
        for (WebSocketServer item : webSocketSet) {
            try {
                item.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }
    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }


    /**
     * 群发自定义消息
     * */
    public static void sendInfo(String message,@PathParam("sid") String sid) throws IOException {
        log.info("推送消息到窗口"+sid+"，推送内容:"+message);
        if (StringUtils.isEmpty(message)) {
            return;
        }

        String msg = cache.append(message).toString();
        if (!message.contains(">") && cache.length() < 2048) {
            return;
        } else {
            msg = msg.replaceAll("\\[m","").replaceAll("\\[\\?1l\u001B>","")
                    .replaceAll("004l","")
                    .replaceAll("\\[\\?1h\u001B=\u001B\\[\\?2004h","");
            cache.setLength(0);
        }
        if (msg.contains(">")) {
            msg = msg + System.lineSeparator();
        }
        for (WebSocketServer item : webSocketSet) {
            try {
                //这里可以设定只推送给这个sid的，为null则全部推送
                if(sid==null) {
                    item.sendMessage(msg);
                }else if(item.sid.equals(sid)){
                    item.sendMessage(msg);
                }
            } catch (IOException e) {
                continue;
            }
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }

    private static String replaceMsg(String msg) {
        String tmp = ansiRemovePattern.matcher(msg)
                .replaceAll("");
        String s = tmp.replaceAll("\\[\\d*m", "");
//        final String s = colorRemovePattern.matcher(tmp)
//                .replaceAll("");
//        String tmp2 = s.replaceAll("=\\.*X", "[X");
//        return tmp.replaceAll("\\[\\d*","").replaceAll("\\[m","");
        return s;
    }


    public static void main(String[] args) {
        String s = "Command Definitions : \n" +
                "  Command-Name                   Command-Description \n" +
                "  plugins                        you can use the \"plugins\" command to list available plugins \n" +
                "  use                            you can use command \"use [plugin_name]\" or \"use [plugin_name@namespace]\" to use the plugin \n" +
                "  top                            system command top \n" +
                "  ps                           \u001B  get all process infos \n" +
                "  jps                            list available java process，under linux, you can use the \"ps\" command to list all processes \n" +
                "  grep                           grep -I [subText] handle content from pipe \n" +
                "  split                          split -f [separator] -i [index] -l [limit] input or split -f [separator] -i [index] -l [limit] if it's a pipe operation \n" +
                "  trim                           removed any leading and trailing whitespace \n" +
                "  session                        Show XPocket Status Context \n" +
                "  cd                             change current context to system \n" +
                "  \u001B[32mhistory                        list command history \u001B[m\n" +
                "  version                        XPocket version info \n" +
                "  clear                          clear the screan \n" +
                "  help,h                         command help info and you can use \"help <cmd>\" to see the detailed usage of the command \n" +
                "  \u001B[32mquit                           quit XPocket \n" +
                "  attach                         \u001B[37mattach on a java process,attach [pid] \n" +
                "  detach                         disconnect with the java process,detach \n" +
                "  \u001Becho                           print input strings \n";
        System.out.println(s.replaceAll("\\[\\d*m", ""));

    }
}


