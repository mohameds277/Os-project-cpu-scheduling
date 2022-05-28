//Ahmad Hussien  	1181285
//Mohammed Qadumy	1180434
//Mohammed Shqierat	1190702
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws FileNotFoundException {

		while (true) {
			float Throuput = 0;
			// generate a file by JAVA
			Scanner S = new Scanner(System.in);
			System.out.println("Enter 1 if you want to generate a file or 2 to read a file from library");
			int s = S.nextInt();
			File file = new File("C:\\Users\\abuhu\\eclipse-workspace\\T\\src\\application\\input2.txt");
			ArrayList<Integer> delays = new ArrayList<Integer>();
			ArrayList<Integer> pid = new ArrayList<Integer>();

			ArrayList<ArrayList<Integer>> CpuBurst = new ArrayList<ArrayList<Integer>>();
			ArrayList<ArrayList<Integer>> IOBurst = new ArrayList<ArrayList<Integer>>();
			ArrayList<Process> P = new ArrayList<Process>();
			if (s == 1) {

				Random random = new Random();
				int numOfProcesses = random.nextInt(100 - 20) + 20;
				int[] CPU_IO = new int[numOfProcesses];
				for (int i = 0; i < numOfProcesses; i++) {
					delays.add((int) random.nextInt(10));
					pid.add(i + 1);
				}
				for (int i = 0; i < numOfProcesses; i++) {
					ArrayList<Integer> CpuBurstperProcess = new ArrayList<Integer>();
					ArrayList<Integer> IOBurstperProcess = new ArrayList<Integer>();
					CPU_IO[i] = random.nextInt(120 - 2) + 2;
					for (int j = 0; j < CPU_IO[i]; j++) {
						CpuBurstperProcess.add(random.nextInt(20 - 5) + 5);
						if (j == CPU_IO[i] - 1) {
							break;
						}
						IOBurstperProcess.add(random.nextInt(20 - 5) + 5);
					}
					CpuBurst.add(CpuBurstperProcess);
					IOBurst.add(IOBurstperProcess);
					P.add(new Process(delays.get(i), pid.get(i), CpuBurstperProcess, IOBurstperProcess));
				}
				for (int j = 1; j < P.size(); j++) {
					P.get(j).setDelay(P.get(j - 1).getDelay() + P.get(j).getDelay());
				}
				try {
					FileWriter fw = new FileWriter(file);
					for (int i = 0; i < P.size(); i++) {
						System.out.println(P.get(i).toString());
						fw.write(P.get(i).toString());
						fw.write("\n/n");
					}
					fw.close();
				} catch (Exception e) {
					System.out.println(e);
				}

			} else {

				File myObj = new File("C:\\Users\\abuhu\\eclipse-workspace\\T\\src\\application\\input.txt");
					Scanner myReader = new Scanner(myObj);
				while (myReader.hasNextLine()) {

					ArrayList<Integer> CpuBurstperProcess = new ArrayList<Integer>();
					ArrayList<Integer> IOBurstperProcess = new ArrayList<Integer>();
					String data = myReader.nextLine();
					String arr[] = data.split(" ");
					delays.add(Integer.parseInt(arr[0]));
					pid.add(Integer.parseInt(arr[1]));
					for (int j = 2; j < arr.length; j++) {
						CpuBurstperProcess.add(Integer.parseInt(arr[j]));
						j++;
						if (j == arr.length) {
							break;
						}
						IOBurstperProcess.add(Integer.parseInt(arr[j]));
					}
					CpuBurst.add(CpuBurstperProcess);
					IOBurst.add(IOBurstperProcess);
					P.add(new Process(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]), CpuBurstperProcess,
							IOBurstperProcess));
				}
			}

			int counter = 1;
			System.out.println("enter 1  for RR and 2 for SRTF");
			int input = S.nextInt();
			float run = 0;
			if (input != 1) {
				for (int d = 1; d < P.size(); d++) {
					if (P.get(d).getDelay() == 0) {
						counter++;
					} else {
						break;
					}
				}

				for (int j = 0; j < counter; j++) {
					P.get(j).setDelay(0);
				}

				for (int j = counter; j < P.size(); j++) {
					P.get(j).setDelay(P.get(j - 1).getDelay() + P.get(j).getDelay());
				}

				int Sum = 0;
				ArrayList<Process> readyQueue = new ArrayList<Process>();

				readyQueue.add(P.get(0));
				for (int j2 = 0; j2 < P.size(); j2++) {

					for (int i = 0; i < P.get(j2).getCPUBurst().size(); i++) {
						Sum = Sum + P.get(j2).getCPUBurst().get(i);
					}
					P.get(j2).getCPUBurst().set(0, Sum);
					run=findavgTime(P, P.size());
				}
			} else {
				System.out.println("please enter the Quantum");
				int Q = S.nextInt();
				for (int d = 1; d < P.size(); d++) {
					if (P.get(d).getDelay() == 0) {
						counter++;
					} else {
						break;
					}
				}

				for (int j = 0; j < counter; j++) {
					P.get(j).setDelay(0);
				}

				for (int j = counter; j < P.size(); j++) {
					P.get(j).setDelay(P.get(j - 1).getDelay() + P.get(j).getDelay());
				}

				int Sum = 0;
				for (int j2 = 0; j2 < P.size(); j2++) {

					for (int i = 0; i < P.get(j2).getCPUBurst().size(); i++) {
						Sum = Sum + P.get(j2).getCPUBurst().get(i);
					}
					Sum = (int) (Sum * Q / (Q + 0.7));
					P.get(j2).getCPUBurst().set(0, Sum);
					run = findavgTime(P, P.size());
				}
			}
			
		}
	}

	public void D1(ArrayList<Process> P) {
		ArrayList<Process> readyQueue = new ArrayList<Process>();
		ArrayList<Process> waitingQueue = new ArrayList<Process>();
		readyQueue.add(P.get(0));
		int j = 0;
		for (j = 0; j < P.size(); j++) {
			Thread one;
			one = new Thread(new Runnable() {
				int i = 0;
				int timer = 0;

				@Override
				public void run() {
					synchronized (P) {
						while (i < P.size()) {

							if (P.get(i).getDelay() <= timer)
								readyQueue.add(P.get(i));
							Thread two = new Thread(new Runnable() {
								int i = 1;

								@Override
								public void run() {

									findavgTime(readyQueue, readyQueue.size());
									timer = readyQueue.get(i).getCPUBurst().get(0);
									waitingQueue.add(readyQueue.get(i));
									synchronized (P) {
										Thread three = new Thread(new Runnable() {
											@Override
											public void run() {
												synchronized (P) {
													readyQueue.add(waitingQueue.get(i));
													waitingQueue.remove(i);
												}
											}
										});
										readyQueue.remove(i);
										if (timer > waitingQueue.get(i - 1).getIOBurst().get(0)) {
											three.start();
										}

									}
								}

							});
							i++;
						}
					}
				}
			});

		}
	}

	static void findWaitingTime(ArrayList<Process> p, int n, int wt[], int k) {
		Process temp = new Process(55, 55, null, null);
		for (int j1 = 0; j1 < p.size() - 1; j1++) {
			for (int j = 0; j < p.size() - 1; j++) {
				if (p.get(j).getCPUBurst().get(0) > p.get(j + 1).getCPUBurst().get(0)) {
					temp.setCPUBurst(p.get(j).getCPUBurst());
					temp.setIOBurst(p.get(j).getIOBurst());
					temp.setPID(p.get(j).getPID());
					temp.setDelay(p.get(j).getDelay());

					p.get(j).setCPUBurst(p.get(j + 1).getCPUBurst());
					p.get(j).setIOBurst(p.get(j + 1).getIOBurst());
					p.get(j).setPID(p.get(j + 1).getPID());
					p.get(j).setDelay(p.get(j + 1).getDelay());

					p.get(j + 1).setCPUBurst(temp.getCPUBurst());
					p.get(j + 1).setIOBurst(temp.getIOBurst());
					p.get(j + 1).setPID(temp.getPID());
					p.get(j + 1).setDelay(temp.getDelay());
				}
			}
		}

		wt[0] = k;
		for (int i = 1; i < p.size(); i++) {
			wt[i] = wt[i - 1] + p.get(i).getCPUBurst().get(0);
		}

	}

	static void findTurnAroundTime(ArrayList<Process> p, int n, int wt[], int tat[]) {
		for (int i = 0; i < n; i++)
			tat[i] = p.get(i).getCPUBurst().get(0) + wt[i];
	}

	static float findavgTime(ArrayList<Process> p, int n) {
		int wt[] = new int[n], tat[] = new int[n];
		int total_wt = 0, total_tat = 0;
		findWaitingTime(p, n, wt, 1);
		findTurnAroundTime(p, n, wt, tat);

		System.out.println("Processes " + " Burst time " + " Waiting time " + " Turn around time");

		for (int i = 0; i < n; i++) {
			total_wt = total_wt + wt[i];
			total_tat = total_tat + tat[i];
			System.out.println(" " + p.get(i).getPID() + "\t\t" + p.get(i).getCPUBurst().get(0) + "\t\t " + wt[i]
					+ "\t\t" + tat[i]);

		}

		System.out.println("Average waiting time = " + (float) total_wt / (float) n);
		System.out.println("Average turn around time = " + (float) total_tat / (float) n);
		int Throuput;
		float CpuUtilization;
		
		CpuUtilization = (float) tat[n-1] / (float)(tat[n-1]- tat[0]) ;
		Throuput = tat[n-1] / p.size();

		System.out.println("CPU UTILIZATION = "+(CpuUtilization*100.0-100.0)+"%");
		System.out.println("Throuput = "+Throuput);
		return (float) total_tat / (float) n;
	
	}
}