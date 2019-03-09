//在庫管理プログラム

class SyncInvertory {
    private int count;

    public SyncInvertory() {
        count = 50;
    }

    public synchronized void ship(int amount) {
        if (count < amount) {
            System.out.println("================ ship() 待機.");
            try {
                wait();
            } catch (InterruptedException e) {}
            System.out.println("================ ship() 解放.");
        }
        count -= amount;
        notify();
        try {
            Thread.sleep((long) (Math.random() * 1000));
        } catch (InterruptedException e) {}
        System.out.println("今回出庫した数 : " + amount);
        System.out.println("現在の在庫数 : " + count);
        System.out.println("----------------------");
    }

    public synchronized void arrive(int amount) {
        if ((count + amount) > 50) {
            System.out.println("================ arrive() 待機.");
            try {
                wait();
            } catch (InterruptedException ignored) {}
            System.out.println("================ arrive() 解放.");
        }
        count += amount;
        notify();
        try {
            Thread.sleep((long) (Math.random() * 1000));
        } catch (InterruptedException e) {}
            System.out.println("今回加算した数 : " + amount);
            System.out.println("現在の在庫数 : " + count);
            System.out.println("----------------------");
        }
    }

    class Seller extends Thread {
        private int amount;
        private SyncInvertory si;

        public Seller(SyncInvertory si, int amount) {
            this.si = si;
            this.amount = amount;
        }

        public void run() {
            while (true) {
                si.ship(amount);
            }
        }
    }

    class Buyer extends Thread {
        private int amount;
        private SyncInvertory si;

        public Buyer(SyncInvertory si, int amount) {
            this.si = si;
            this.amount = amount;
        }

        public void run() {
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

*/