import java.util.Properties
import java.sql.DriverManager

// Load review and date data from intermidiate parquet files
val review = spark.read.parquet("gs://yelp-dataset-adbms/parquet/review/*");
val tips = spark.read.parquet("gs://yelp-dataset-adbms/parquet/tips/*");

// Extract day, month and year from review and tips dates
val review_df1 = review.withColumn("day", date_format(to_date($"date", "yyyy-MM-dd HH:mm:ss"), "dd")).withColumn("month", date_format(to_date($"date", "yyyy-MM-dd HH:mm:ss"), "MM")).withColumn("year", date_format(to_date($"date", "yyyy-MM-dd HH:mm:ss"), "yyyy"))
val tips_df1 = tips.withColumn("day", date_format(to_date($"date", "yyyy-MM-dd HH:mm:ss"), "dd")).withColumn("month", date_format(to_date($"date", "yyyy-MM-dd HH:mm:ss"), "MM")).withColumn("year", date_format(to_date($"date", "yyyy-MM-dd HH:mm:ss"), "yyyy"))

// Group by day, month and year to get all possible dates in review and tips
val review_df2 = review_df1.groupBy("day", "month", "year").count().drop("count");
val tips_df2 = tips_df1.groupBy("day", "month", "year").count().drop("count");

// Union review and tips dates
val dates_df = review_df2.union(tips_df2);

// set credentials
val jdbcPort = "3306"
val jdbcDatabase = "adbm_analytical"
val jdbcUsername = "root"
val jdbcPassword = "adbm-root"   // the password you set
val jdbcHostname = "35.227.105.111"


val jdbcUrl =s"jdbc:mysql://$jdbcHostname:$jdbcPort/$jdbcDatabase?character_set_server=utf8mb4&character_set_client=utf8mb4&character_set_connection=utf8mb4&character_set_database=utf8mb4&character_set_results=utf8mb4&collation_connection=utf8mb4_unicode_ci&collation_database=utf8mb4_unicode_ci&collation_server=utf8mb4_unicode_ci"

val driverClass = "com.mysql.jdbc.Driver"
Class.forName(driverClass)  // check jdbc driver

// set connection properties
val connectionProperties = new Properties()
connectionProperties.put("user", s"$jdbcUsername")
connectionProperties.put("password", s"$jdbcPassword")
connectionProperties.setProperty("Driver", driverClass)

// first drop the table if it already exists
val connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword)
assert(!connection.isClosed)
val stmt = connection.createStatement()

stmt.executeUpdate("drop table if exists dates")
stmt.executeUpdate("""CREATE TABLE dates (
  date_id BIGINT primary key AUTO_INCREMENT,
  day SMALLINT not null,
  month SMALLINT not null,
  year SMALLINT not null
 )""");

dates_df.write.mode("append").jdbc(jdbcUrl, "dates", connectionProperties)