import java.util.Properties
import java.sql.DriverManager

// Load business ODB data from intermediate parquet file
val business = spark.read.parquet("gs://yelp-dataset-adbms/parquet/business/*");

// Group the data by name of the restaurant
val business_adb = business.groupBy("name").agg(max($"categories")).withColumnRenamed("max(categories)", "categories")

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

stmt.executeUpdate("drop table if exists business")

stmt.executeUpdate("""CREATE TABLE business (
  business_id BIGINT primary key AUTO_INCREMENT,
  name VARCHAR(128) not null,
  categories TEXT
 )""");
 
 business_adb.write.mode("append").jdbc(jdbcUrl, "business", connectionProperties)