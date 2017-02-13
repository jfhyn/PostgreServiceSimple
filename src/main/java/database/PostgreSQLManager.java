package database;

import entities.Post;

import java.io.IOException;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by numash on 27.01.2017.
 */
public class PostgreSQLManager {
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String driver;
    private String connectionString;
    private String username;
    private String password;

    public PostgreSQLManager() throws IOException {

        /*Properties properties = new Properties();
        InputStream input = null;

        try {
            //load properties from file
            //input = new FileInputStream("PostgreTomcat-1.0\\WEB-INF\\classes\\config.properties");
            input = new FileInputStream("./src/main/resources/config.properties");
            properties.load(input);
            driver = properties.getProperty("driver");
            connectionString = properties.getProperty("connectionString");
            username = properties.getProperty("username");
            password = properties.getProperty("password");

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                input.close();
            }
        }*/
    }

    public void connectToDatabase(String name) throws SQLException {

        Connection conn = null;
        Statement stmt = null;

        try {
            conn = openConnection();
            conn.setAutoCommit(false);
            stmt = conn.createStatement();

            createRoleIfNotExist(stmt, username, password);
            createDatabaseIfNotExist(name, username, password);

            stmt.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            conn.close();
        }
    }

    public void createMasterTable() throws SQLException {
        String query = "CREATE TABLE IF NOT EXISTS posts(\n" +
                "  \"post_id\" serial NOT NULL,\n" +
                "  \"publish_date\" timestamp without time zone NOT NULL DEFAULT now(),\n" +
                "  \"username\" varchar(15) NOT NULL,\n" +
                "  \"post_text\" varchar(140) NOT NULL);\n";
        Connection conn = null;

        try {
            conn = openConnection();
            conn.setAutoCommit(false);

            Statement stmt = conn.createStatement();

            stmt.executeUpdate(query);
            stmt.close();
            conn.commit();
            System.out.println("Table is created");

        } catch (Exception e) {
            e.printStackTrace();
        }

        finally {
            conn.close();
        }
    }

    public void createPartitionFunction() throws SQLException {
        String query =
                "CREATE OR REPLACE FUNCTION posts_insert_trigger()\n" +
                        "    RETURNS trigger AS\n" +
                        "$BODY$\n" +
                        "DECLARE\n" +
                        "    table_master    varchar(255)        := 'posts';\n" +
                        "    table_part      varchar(255)        := '';\n" +
                        "BEGIN\n" +
                        "    -- Даём имя партиции --------------------------------------------------\n" +
                        "    table_part := table_master \n" +
                        "                    || '_y' || date_part( 'year', NEW.publish_date )::text \n" +
                        "                    || '_m' || date_part( 'month', NEW.publish_date )::text \n" +
                        "                    || '_d' || date_part( 'day', NEW.publish_date )::text;\n" +
                        "\n" +
                        "    -- Проверяем партицию на существование --------------------------------\n" +
                        "    PERFORM 1 FROM pg_class \n" +
                        "    WHERE relname = table_part\n" +
                        "    LIMIT 1;\n" +
                        "    -- Если её ещё нет, то создаём --------------------------------------------\n" +
                        "    IF NOT FOUND\n" +
                        "    THEN\n" +
                        "        -- Cоздаём партицию, наследуя мастер-таблицу --------------------------\n" +
                        "        EXECUTE '\n" +
                        "            CREATE TABLE ' || table_part || ' ( )\n" +
                        "            INHERITS ( ' || table_master || ' )';\n" +
                        "        -- Создаём индексы для текущей партиции -------------------------------\n" +
                        "        EXECUTE '\n" +
                        "            CREATE INDEX ' || table_part || '_post_id_date_index\n" +
                        "            ON ' || table_part || '\n" +
                        "            USING btree\n" +
                        "            (post_id, publish_date)';\n" +
                        "    END IF;\n" +
                        "    -- Вставляем данные в партицию --------------------------------------------\n" +
                        "    EXECUTE '\n" +
                        "        INSERT INTO ' || table_part || ' \n" +
                        "        SELECT ( (' || quote_literal(NEW) || ')::' || TG_RELNAME || ' ).*';\n" +
                        "\n" +
                        "    RETURN NULL;\n" +
                        "END;\n" +
                        "$BODY$\n" +
                        "LANGUAGE plpgsql VOLATILE;\n" +
                        "DROP TRIGGER IF EXISTS posts_insert_trigger ON posts;\n" +
                        "CREATE TRIGGER posts_insert_trigger\n" +
                        "  BEFORE INSERT\n" +
                        "  ON posts\n" +
                        "  FOR EACH ROW\n" +
                        "  EXECUTE PROCEDURE posts_insert_trigger();";

        Connection conn = null;

        try {
            conn = openConnection();
            conn.setAutoCommit(false);

            Statement stmt = conn.createStatement();

            stmt.executeUpdate(query);
            stmt.close();
            conn.commit();
            System.out.println("Trigger is created");

        } catch (Exception e) {
            e.printStackTrace();
        }

        finally {
            conn.close();
        }
    }

    public void insertIntoPosts(String name, String text) throws SQLException {
        Connection conn = null;
        Statement stmt = null;

        try {
            conn = openConnection();
            conn.setAutoCommit(false);

            stmt = conn.createStatement();
            Date date = new Date();

            String sql = "INSERT INTO Posts(publish_date, username, post_text) "
                    + "VALUES ('" + dateFormat.format(date) + "', '" + name + "', '" + text + "');";
            stmt.executeUpdate(sql);
            stmt.close();
            conn.commit();

        } catch(Exception e){
            e.printStackTrace();
        }
        finally {
            conn.close();
        }
    }

    public int countPosts() throws SQLException{
        int postNumber = 0;

        Connection conn = null;

        try{
            conn = openConnection();
            conn.setAutoCommit(false);

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT COUNT(*) AS number FROM posts;");
            postNumber = rs.next() ? rs.getInt("number") : 0;
            rs.close();
            stmt.close();

        } catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            conn.close();
        }
        return postNumber;
    }

    public List<Post> selectAllPosts() throws SQLException {
        Connection conn = null;

        try{
            conn = openConnection();
            conn.setAutoCommit(false);

            List<Post> notes = new ArrayList<Post>();

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM posts ORDER BY post_id;" );
            while ( rs.next() ) {
                Post note = new Post();
                note.setDate(rs.getString("publish_date"));
                note.setUsername(rs.getString("username"));
                note.setText(rs.getString("post_text"));
                notes.add(note);
            }
            rs.close();
            stmt.close();

            return notes;
        } catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            conn.close();
        }
        return null;
    }

    public List<Post> selectPosts(int pageNumber) throws SQLException {
        Connection conn = null;

        try{
            conn = openConnection();
            conn.setAutoCommit(false);

            List<Post> notes = new ArrayList<Post>();

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM posts ORDER BY post_id DESC LIMIT 5 OFFSET " + (pageNumber - 1) * 5 + ";");
            while ( rs.next() ) {
                Post note = new Post();
                note.setDate(rs.getString("publish_date"));
                note.setUsername(rs.getString("username"));
                note.setText(rs.getString("post_text"));
                notes.add(note);
            }
            rs.close();
            stmt.close();

            return notes;
        } catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            conn.close();
        }
        return null;
    }

    private void createDatabaseIfNotExist(String name, String role, String password) throws SQLException {
        Connection conn = null;
        Statement stmt = null;

        String query = "DO\n" +
                "$do$\n" +
                "DECLARE\n" +
                "  _db TEXT := '" + name + "'; " +
                "  _user TEXT := '" + role + "'; " +
                "  _password TEXT := '" + password + "'; " +
                "BEGIN\n" +
                "CREATE EXTENSION IF NOT EXISTS dblink; \n" +
                "  IF NOT EXISTS (SELECT * FROM pg_database WHERE datname = _db) THEN\n" +
                "    PERFORM dblink_connect('host=localhost user=' || _user || ' password=' || _password || ' dbname=' || current_database());\n" +
                "    PERFORM dblink_exec('CREATE DATABASE ' || _db);\n" +
                "    PERFORM dblink_connect('host=localhost user=' || _user || ' password=' || _password || ' dbname=' || _db);\n" +
                "  END IF;" +
                "END\n" +
                "$do$\n";

        try{
            conn = openConnection();
            stmt = conn.createStatement();
            stmt.execute(query);
            stmt.close();
            System.out.println("Connected to database");
            conn.commit();
        }catch(SQLException e){
            e.printStackTrace();
        }
        finally {
            conn.close();
        }
    }

    private void createRoleIfNotExist(Statement stmt, String role, String password) throws SQLException {
        Connection conn = null;

        String query = "DO\n" +
                "$body$\n" +
                "BEGIN\n" +
                "   IF NOT EXISTS (\n" +
                "      SELECT *\n" +
                "      FROM   pg_catalog.pg_user\n" +
                "      WHERE  usename = '" + role + "') THEN\n" +
                "      --CREATE ROLE " + role + " LOGIN PASSWORD '" + password + "';\n" +
                "   END IF;\n" +
                "END\n" +
                "$body$;";
        try {
            conn = openConnection();
            stmt.executeUpdate(query);
            stmt.close();
            conn.commit();
            System.out.println("Role is created. ");

        }catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            conn.close();
        }
    }

    private Connection openConnection(){
        try {
            Class.forName("org.postgresql.Driver");
            //Class.forName(driver);
            return DriverManager.getConnection("jdbc:postgresql://localhost:5432/service","postgres","admin");
            //return DriverManager.getConnection(connectionString,username,password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
