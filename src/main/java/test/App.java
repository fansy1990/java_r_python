package test;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

/**
 * 调用远程R脚本，并获取返回值
 * 需要提前在hostname对应的路径下建立对应的脚本(如/tmp/area.r)，脚本如下：
 *  area<-function(r){pi*r^2}
 *
 */
public class App 
{
    public static void main(String[] args) {
        String hostname = "192.168.9.148";
        String rScript = "/tmp/area.r";
        try {
            callRScript(hostname,rScript);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void callRScript(String hostname,String rScript) throws RserveException, REXPMismatchException {
        RConnection rc = new RConnection(hostname);
        // source函数需要给出R脚本路径, 注意传入转义的引号
        rc.eval("source(\""+rScript+"\")");
        REXP rexp = rc.eval("area(10)");
        System.out.println("Area of 10 is " + rexp.asDouble());
    }
}
