import java.util.Properties
import java.sql.DriverManager

// Loading the raw data from a GCP bucket
val raw = spark.read.json("gs://yelp-dataset-adbms/yelp_dataset").cache();

// Selecting required data out of the raw data
val business = raw.select("business_id", "name", "address", "city", "postal_code", "latitude", "longitude", "stars", "review_count", "categories").filter("business_id is not null and name is not null and user_id is null");
val user = raw.select("user_id", "name", "review_count", "yelping_since", "friends", "useful", "funny", "cool", "fans", "elite", "average_stars").filter("user_id is not null and business_id is null");
val tips = raw.select("business_id", "user_id", "compliment_count", "text", "date").filter("user_id is not null and business_id is not null and review_id is null");
val review = raw.select("review_id", "user_id", "business_id", "stars", "date", "text", "useful", "funny", "cool").filter("review_id is not null")

// Creating temporary views so as to insert data into database
business.createOrReplaceTempView("business");
user.createOrReplaceTempView("user");
tips.createOrReplaceTempView("tips");
review.createOrReplaceTempView("review");

// set database credentials
val jdbcPort = "3306"
val jdbcDatabase = "adbm"
val jdbcUsername = "root"
val jdbcPassword = "********" 
val jdbcHostname = "35.227.105.111"

// 
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

stmt.executeUpdate("drop table if exists business")
stmt.executeUpdate("drop table if exists user")
stmt.executeUpdate("drop table if exists tips")
stmt.executeUpdate("drop table if exists review")

stmt.executeUpdate("""CREATE TABLE business (
  business_id VARCHAR(32) not null,
  name VARCHAR(128) not null,
  address VARCHAR(128),
  city VARCHAR(128),
  postal_code VARCHAR(8),
  latitude DOUBLE,
  longitude DOUBLE,
  stars DOUBLE,
  review_count BIGINT,
  categories TEXT
 )""");
 
 stmt.executeUpdate("""CREATE TABLE user (
  user_id VARCHAR(32) not null,
  name VARCHAR(128) not null,
  review_count BIGINT,
  yelping_since VARCHAR(32),
  friends LONGTEXT,
  useful BIGINT,
  funny BIGINT,
  cool BIGINT,
  fans BIGINT,
  elite VARCHAR(512),
  average_stars DOUBLE
 )""");
 
 stmt.executeUpdate(""" CREATE TABLE tips (
  business_id VARCHAR(32) not null,
  user_id VARCHAR(32) not null,
  compliment_count BIGINT,
  text LONGTEXT,
  date VARCHAR(32)
 )""");
 
 stmt.executeUpdate("""CREATE TABLE review (
  review_id VARCHAR(32) not null,
  user_id VARCHAR(32) not null,
  business_id VARCHAR(32) not null,
  stars DOUBLE,
  date VARCHAR(32),
  text LONGTEXT,
  useful BIGINT,
  funny BIGINT,
  cool BIGINT
)""");

// write the dataframe to a table called "interaction"
business.write.mode("append").jdbc(jdbcUrl, "business", connectionProperties)
user.write.mode("append").jdbc(jdbcUrl, "user", connectionProperties)
tips.write.mode("append").jdbc(jdbcUrl, "tips", connectionProperties)
review.write.mode("append").jdbc(jdbcUrl, "review", connectionProperties)

// Store the data as intermediate data sources for further ETL
business.write.parquet("gs://yelp-dataset-adbms/parquet/business")
user.write.parquet("gs://yelp-dataset-adbms/parquet/user")
tips.write.parquet("gs://yelp-dataset-adbms/parquet/tips")
review.write.parquet("gs://yelp-dataset-adbms/parquet/review")