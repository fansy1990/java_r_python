package test;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.python.util.PythonInterpreter;

import java.io.*;
import java.util.Properties;

/**
 * Created by fansy on 2017/11/9.
 */
public class PythonTest {
    public static void main(String[] args) {
        init();
        test1();// 直接执行Python命令
        test2();// 执行本地Python脚本
        test3();// 执行远程Python脚本
    }

    public static void test1(){
        // 直接执行Python语句
        PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.exec("days=('mod','Tue','Wed','Thu','Fri','Sat','Sun'); ");
        interpreter.exec("print days[1];");
    }

    public static void test2(){
        // 执行脚本
        PythonInterpreter interpreter = new PythonInterpreter();
        File file = new File("./python_scripts/test.py");
        interpreter.execfile(file.getAbsolutePath());
    }

    public static void test3(){
        // 执行远程脚本
        String file = "/tmp/test.py";
        String exec = exec("192.168.9.148", "root", "123456", 22, " python "+file);
        System.out.println(exec);
    }


    public static String exec(String host,String user,String psw,int port,String command){
        StringBuffer buff= new StringBuffer();
        Session session =null;
        ChannelExec openChannel =null;
        try {
            JSch jsch=new JSch();
            session = jsch.getSession(user, host, port);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");//跳过公钥的询问
            session.setConfig(config);
            session.setPassword(psw);
            session.connect(5000);//设置连接的超时时间
            openChannel = (ChannelExec) session.openChannel("exec");
            openChannel.setCommand(command); //执行命令
            int exitStatus = openChannel.getExitStatus(); //退出状态为-1，直到通道关闭
            System.out.println(exitStatus);
// 下面是得到输出的内容
            openChannel.connect();
            InputStream in = openChannel.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String buf = null;
            while ((buf = reader.readLine()) != null) {
                buff.append(buf+"\n");
            }
        } catch (JSchException e) {
            e.printStackTrace();
        } catch (IOException e) {
            buff.append(e.getMessage()+"\n");
        } finally{
            if(openChannel!=null&&!openChannel.isClosed()){
                openChannel.disconnect();
            }
            if(session!=null&&session.isConnected()){
                session.disconnect();
            }
        }
        return buff.toString();
    }
    

    private static void init(){
        Properties props = new Properties();
//        props.put("python.home", "path to the Lib folder");
        props.put("python.console.encoding", "UTF-8");
        props.put("python.security.respectJavaAccessibility", "false");
        props.put("python.import.site", "false");
        Properties preprops = System.getProperties();
        PythonInterpreter.initialize(preprops, props, new String[0]);
    }
}
