import java.sql.*;
import java.util.Scanner;

public class Main {

    private static Connection connection;
    private static Statement statement;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        boolean exit = true;

        connect();

        // в методе выполняются пункты 1 и 2
        prepareDB();

        while(exit){

            System.out.println("Список команд:\n/цена  «имя товара»\n/сменитьцену «имя товара» «цена»\n" +
                    "/товарыпоцене  «начало диапазона» «конец диапазона»\n/выход");
            String[] data = scanner.nextLine().split("\\s");
            PreparedStatement ps = null;
            switch (data[0]){

                case "/выход":
                    exit = false;
                    break;

                //3 Написать консольное приложение, которое позволяет узнать цену товара по его имени, либо
                //вывести сообщение «Такого товара нет», если товар не обнаружен в базе. Консольная
                //команда: «/цена товар545»
                case "/цена":
                    try {
                        ps = connection.prepareStatement("SELECT cost FROM store WHERE title = ?");
                        ps.setString(1, data[1]);
                        ResultSet rs = ps.executeQuery();
                        System.out.println("цена товара \"" + data[1] + "\" " + rs.getInt(1));
                        System.out.println();
                    } catch (SQLException e) {
                        System.out.println("Такого товара нет");//возможно, существует другой способ написания этого сообщения
                        System.out.println();
                        //e.printStackTrace();
                    }
                    break;

//              4. Добавить возможность изменения цены товара. Указываем имя товара и новую цену.
//              Консольная команда: «/сменитьцену товар10 10000».
                case "/сменитьцену":
                    try {
                        ps = connection.prepareStatement("UPDATE store SET cost = ? WHERE title = ?");
                        ps.setInt(1, Integer.valueOf(data[2]));
                        ps.setString(2, data[1]);
                        ps.executeUpdate();
                        System.out.println("успешно!");
                        System.out.println();
                    } catch (SQLException e) {
                        System.out.println("Такого товара нет");
                        System.out.println();
                        //e.printStackTrace();
                    }
                    break;

//              5. Вывести товары в заданном ценовом диапазоне. Консольная команда: «/товарыпоцене 100
//              600».
                case "/товарыпоцене":
                    try {
                        ps = connection.prepareStatement("SELECT * FROM store WHERE cost >= ? and cost <= ?");
                        ps.setInt(1, Integer.valueOf(data[1]));
                        ps.setInt(2, Integer.valueOf(data[2]));
                        ResultSet rs = ps.executeQuery();
                        System.out.println("-prodID--Title--Cost-");
                        while (rs.next()){
                            System.out.println("    " + rs.getInt("prodid") + "    " +
                                    rs.getString("title") + "  " + rs.getInt("cost"));
                        }
                        System.out.println();
                    } catch (SQLException e) {
                        System.out.println("Таких товаров нет");
                        System.out.println();
                        //e.printStackTrace();
                    }
                    break;
                default:
                    System.out.println("команда введена не корректно. попробуйте еще раз.");
                    System.out.println();
                    break;
            }
        }

        disconnect();
    }


    public static void prepareDB(){
        try {
            statement = connection.createStatement();

            //1. Сформировать таблицу товаров (id, prodid, title, cost) запросом из Java-приложения:
            statement.execute("CREATE TABLE IF NOT EXISTS store (id INTEGER PRIMARY KEY, prodid INTEGER, title  TEXT, cost INTEGER );");

            //2. При запуске приложения очистить таблицу и заполнить 10000 товаров
            statement.execute("DELETE FROM store");
            for (int i = 1; i <= 5; i++) {
                statement.execute("INSERT INTO store (prodid, title, cost) VALUES (" + i +", 'товар" + i + "', " + i*10 + ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void connect(){
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:MainDB.db");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void disconnect(){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
