import java.util.List;
import java.util.Scanner;

public class SimpleDB {
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		String input = in.nextLine();
		DB<String, Integer> data = new DB<String, Integer>();

		do {
			String[] cmd = input.split(" ");

			if (cmd[0].equals("SET")) {
				data.set(cmd[1], Integer.parseInt(cmd[2]));
			} else if (cmd[0].equals("GET")) {
				Integer value = data.get(cmd[1]);
				if (value == null) {
					System.out.println("NULL");
				} else {
					System.out.println(value);
				}
			} else if (cmd[0].equals("UNSET")) {
				data.unset(cmd[1]);
			} else if (cmd[0].equals("EQUALTO")) {
				List<String> keys = data.equalTo(Integer.parseInt(cmd[1]));
				if (keys == null || keys.isEmpty()) {
					System.out.println("NONE");
				} else {
					for (String k : keys) {
						System.out.print(k + " ");
					}
					System.out.println();
				}
			} else if (cmd[0].equals("BEGIN")) {
				data.begin();
			} else if (cmd[0].equals("ROLLBACK")) {
				if (!data.rollback()) {
					System.out.println("INVALID ROLLBACK");
				}
			} else if (cmd[0].equals("COMMIT")) {
				data.commit();
			} else {
				System.err.println("Unknown command: " + cmd[0]);
			}
			input = in.nextLine();
		} while (!input.equals("END"));
	}
}
