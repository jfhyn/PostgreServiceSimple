package entities;

/**
 * Created by numash on 27.01.2017.
 */
public class Post {
        private String username;
        private String text;
        private String date;

        public String getUsername() {
            return username;
        }

        public String getText() {
        return text;
    }

        public String getDate() {
        return date;
    }

        public void setUsername(String name) {
            this.username = name;
        }

        public void setText(String test) {
            this.text = test;
        }

        public void setDate(String date) {
            this.date = date;
        }

}
