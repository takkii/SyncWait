//在庫管理プログラム

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class Annotation {

    // チェッカー 独自アノテーション
    public @interface Check {
        boolean value() default true;
    }
    public @interface status {
        String value();
        int id();
    }
}

class SyncInvert {

    @Annotation.Check(false) //数値定義
    @Annotation.status(id = 1, value = "stock")
    private int count; //在庫数

    SyncInvert() {
        count = 50; //在庫数制限
    }

    synchronized void ship(int amount) throws InterruptedException {
        //在庫が足りるか
        if (count < amount) {
            System.out.println("================ ship() 待機.");
            try {
                wait(); //入庫されるまで待ち状態
            } catch (InterruptedException ignored) {}
            System.out.println("================ ship() 解放.");
        }
        //在庫数の減算
        count -= amount;
        notify(); //待ち状態のスレッドへ解除通知
        Thread.sleep((long) (Math.random() * 1000));
        System.out.println("今回出庫した数 : " + amount);
        System.out.println("現在の在庫数 : " + count);
        System.out.println("----------------------");
    }

    synchronized void arrive(int amount) throws InterruptedException {
        //在庫最大数
        if ((count + amount) > 50) {
            System.out.println("================ arrive() 待機.");
            //出庫待ち
            try {
                wait(); //出庫されるまで待ち状態
            } catch (InterruptedException ignored) {}
            System.out.println("================ arrive() 解放.");
        }
        //在庫数の加算
        count += amount;
        notify(); //待ち状態のスレッドへ解除通知
        Thread.sleep((long) (Math.random() * 1000));
        System.out.println("今回加算した数 : " + amount);
            System.out.println("現在の在庫数 : " + count);
            System.out.println("----------------------");
        }
    }

    class Seller extends Thread {

        @Annotation.Check(false) //数値定義
        @Annotation.status(id = 2, value = "deductions")
        private int amount; //引き落とし数
        private SyncInvert si;

        Seller(SyncInvert si, int amount) {
            this.si = si;
            this.amount = amount;
        }

        public void run() {
            //在庫数減産用メソッド呼び出し
            while (true) {
                try {
                    si.ship(amount);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    class Buyer extends Thread {

        @Annotation.Check(false) //数値定義
        @Annotation.status(id = 3, value = "replenishment")
        private int amount; //補充数
        private SyncInvert si;

        Buyer(SyncInvert si, int amount) {
            this.si = si;
            this.amount = amount;
        }

        public void run() {
            // 在庫補充用メソッド呼び出し
            while (true) {
                try {
                    si.arrive(amount);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    @Annotation.Check() // mainメソッド含むSyncWaitクラス
    public class SyncWait {
        public static void main(String[] args) {
            SyncInvert si = new SyncInvert();
            Seller seller = new Seller(si, 15);
            Buyer buyer = new Buyer(si, 30);

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("意気込みをどうぞ!...終了は Ctrl+C または▲などの停止ボタン!");
            String hello = null;
            try {
                hello = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                System.out.println(hello + "...返答をありがとう...シュミレーション開始...");
                seller.start();
                buyer.start();
            }
        }
    }

/*

今回出庫した数 : 15
現在の在庫数 : 35
----------------------
今回出庫した数 : 15
現在の在庫数 : 20
----------------------
今回加算した数 : 30
現在の在庫数 : 50
----------------------
================ arrive() 待機.
今回出庫した数 : 15
現在の在庫数 : 35
----------------------
今回出庫した数 : 15
現在の在庫数 : 20
----------------------
今回出庫した数 : 15
現在の在庫数 : 5
----------------------
================ ship() 待機.
================ arrive() 解放.
今回加算した数 : 30
現在の在庫数 : 35
----------------------
================ arrive() 待機.
================ ship() 解放.
今回出庫した数 : 15
現在の在庫数 : 20
----------------------
今回出庫した数 : 15
現在の在庫数 : 5
----------------------
================ ship() 待機.
================ arrive() 解放.
今回加算した数 : 30
現在の在庫数 : 35
----------------------
================ arrive() 待機.
================ ship() 解放.
今回出庫した数 : 15
現在の在庫数 : 20
----------------------

~~~繰り返し~~~
*/