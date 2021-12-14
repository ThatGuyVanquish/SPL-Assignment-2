import bgu.spl.mics.TickBroadcast;

import java.util.Timer;
import java.util.TimerTask;

public class MainTest {
    public static void main(String[] args) {
        Timer timer = new Timer();
        final Integer[] i = {0};
        while (i[0]<20)
        timer.schedule(new TimerTask(){
            @Override
            public void run() {
                new TickBroadcast(); i[0]++;
            }
        }, 2);
        timer.cancel();
        System.out.println(i[0]);
     //   timer.cancel();
    }
}
