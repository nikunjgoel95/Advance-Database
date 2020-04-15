import java.util.Properties
import java.sql.DriverManager

// Load ODB data from intermedaite parquet file
val business_odb = spark.read.parquet("gs://yelp-dataset-adbms/parquet/business/*")
val review = spark.read.parquet("gs://yelp-dataset-adbms/parquet/review/*")
val tips = spark.read.parquet("gs://yelp-dataset-adbms/parquet/tips/*")

// Extract day, month and year from date data
val review_df1 = review.withColumn("day", date_format(to_date($"date", "yyyy-MM-dd HH:mm:ss"), "dd")).withColumn("month", date_format(to_date($"date", "yyyy-MM-dd HH:mm:ss"), "MM")).withColumn("year", date_format(to_date($"date", "yyyy-MM-dd HH:mm:ss"), "yyyy"))
val tips_df1 = tips.withColumn("day", date_format(to_date($"date", "yyyy-MM-dd HH:mm:ss"), "dd")).withColumn("month", date_format(to_date($"date", "yyyy-MM-dd HH:mm:ss"), "MM")).withColumn("year", date_format(to_date($"date", "yyyy-MM-dd HH:mm:ss"), "yyyy"))

// Load dimensions data from intermediate parquet file
val business = spark.read.parquet("gs://yelp-dataset-adbms/parquet/adb/business/*");
val location = spark.read.parquet("gs://yelp-dataset-adbms/parquet/adb/location/*");
val dates = spark.read.parquet("gs://yelp-dataset-adbms/parquet/adb/dates/*");


// Create temp view for SQL manipulation
business_odb.createOrReplaceTempView("business_odb");
business.createOrReplaceTempView("business");
location.createOrReplaceTempView("location");
review_df1.createOrReplaceTempView("review");
tips_df1.createOrReplaceTempView("tips");

// Create review_fact data frame
val review_fact = spark.sql("select r.stars, (select max(b.business_id) from business as b where b.name = b_odb.name) as business_id, (select max(l.location_id) as location_id from location as l where l.city = b_odb.city and l.state = b_odb.state and l.postal_code = b_odb.postal_code) as location_id, (select max(d.date_id) as date_id from dates as d where d.day = r.day and d.month = r.month and d.year = r.year) as date_id, user_id from review as r, business_odb as b_odb where r.business_id = b_odb.business_id");

// Create tips_fact from data frame
val tips_fact = spark.sql("select compliment_count, text, (select max(b.business_id) from business as b where b.name = b_odb.name) as business_id, (select max(l.location_id) as location_id from location as l where l.city = b_odb.city and l.state = b_odb.state and l.postal_code = b_odb.postal_code) as location_id, (select max(d.date_id) as date_id from dates as d where d.day = t.day and d.month = t.month and d.year = t.year) as date_id, user_id from tips as t, business_odb as b_odb where t.business_id = b_odb.business_id");

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

stmt.executeUpdate("drop table if exists review_fact")
stmt.executeUpdate("drop table if exists tips_fact")

stmt.executeUpdate("""CREATE TABLE `review_fact` (
  `user_id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `business_id` bigint(20) NOT NULL,
  `location_id` bigint(20) NOT NULL,
  `date_id` bigint(20) NOT NULL,
  `stars` double NOT NULL,
  KEY `business_user_idx` (`business_id`,`user_id`),
  KEY `business_location_idx` (`business_id`,`location_id`),
  KEY `user_location_idx` (`user_id`,`location_id`)
)""");

stmt.executeUpdate("""CREATE TABLE `tips_fact` (
  `user_id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `business_id` bigint(20) NOT NULL,
  `location_id` bigint(20) NOT NULL,
  `date_id` bigint(20) NOT NULL,
  `text` longtext COLLATE utf8mb4_unicode_ci,
  `compliment_count` bigint(20) DEFAULT NULL
)""");

review_fact.write.mode("append").jdbc(jdbcUrl, "review_fact", connectionProperties)
tips_fact.write.mode("append").jdbc(jdbcUrl, "tips_fact", connectionProperties)