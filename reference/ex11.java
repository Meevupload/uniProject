class ex11 { 
	public int number = 100; // public number 

	// synchronized method means that threads wont compete over the same data (creates a t1, t2, t1 queue)
	public synchronized void decrement(){  //public method to decrease number by 1
		number--;
		System.out.println("Decrement by one = " + number);
	}


	public static void main(String[] args) { 
		ex11 Ex11 = new ex11(); // creation of main class object
		Thread t1 = new Thread(new Decrement(Ex11)); //creation of two threads that implement the Decrement class
		Thread t2 = new Thread(new Decrement(Ex11));
		t1.start(); //begin threads
		t2.start();
	}
}

class Decrement implements Runnable { // Decrement class 
	ex11 x = null;


	public Decrement(ex11 _x) { //constructor method for each thread
		x = _x; // import number and save to local variable
	}

	public void run(){ //what the thread will do while running
	while(x.number > 0) {
			try { 
				x.decrement();
				Thread.sleep(50); //thread timer (required)
			}catch(InterruptedException e) {} //catch exception (required)
		}
	}
}
