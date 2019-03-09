//在庫管理プログラム

class SyncInvertory {
    private int count; //在庫数

    public SyncInvertory() {
        count = 50;
    }

    public synchronized void ship(int amount) {
        //在庫が足りるか
        if (count < amount) {
            System.out.println("================ ship() 待機.");
            //入庫待ち
            try {
                wait(); //入庫されるまで待機
            } catch (InterruptedException e) {}
            System.out.println("================ ship() 解放.");
        }
        //在庫数の減算
        count -= amount;
        notify(); //待ち状態のスレッドへ解除通知
        try {
            Thread.sleep((long) (Math.random() * 1000));
        } catch (InterruptedException e) {}
        System.out.println("今回出庫した数 : " + amount);
        System.out.println("現在の在庫数 : " + count);
        System.out.println("----------------------");
    }

    public synchronized void arrive(int amount) {
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
        try {
            Thread.sleep((long) (Math.random() * 1000));
        } catch (InterruptedException e) {}
            System.out.println("今回加算した数 : " + amount);
            System.out.println("現在の在庫数 : " + count);
            System.out.println("----------------------");
        }
    }

    class Seller extends Thread {
        private int amount; //引き落とし数
        private SyncInvertory si;

        public Seller(SyncInvertory si, int amount) {
            this.si = si;
            this.amount = amount;
        }

        public void run() {
            //在庫数減産用メソッド呼び出し
            while (true) {
                si.ship(amount);
            }
        }
    }

    class Buyer extends Thread {
        private int amount; //補充数
        private SyncInvertory si;

        public Buyer(SyncInvertory si, int amount) {
            this.si = si;
            this.amount = amount;
        }

        public void run() {
            // 在庫補充用メソッド呼び出し
            while (true) {
                si.arrive(amount);
            }
        }
    }

    public class SyncWait {
        public static void main(String[] args) {
            SyncInvertory si = new SyncInvertory();
            Seller seller = new Seller(si, 15);
            Buyer buyer = new Buyer(si, 30);

            seller.start();
            buyer.start();
        }
    }

/*
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
            今回加算した数 : 30
            現在の在庫数 : 35
            ----------------------

            ~~~繰り返し~~~
*/