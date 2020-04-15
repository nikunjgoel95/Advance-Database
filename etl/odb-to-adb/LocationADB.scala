import java.util.Properties
import java.sql.DriverManager

// Load business data from intermediate parquet file
val business = spark.read.parquet("gs://yelp-dataset-adbms/parquet/business/*");

// Group by state, city and postal_code to get location dimension data
val locations = business.groupBy("state", "city", "postal_code").count().drop("count")

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

stmt.executeUpdate("drop table if exists location")

stmt.executeUpdate("""CREATE TABLE location (
  location_id BIGINT primary key AUTO_INCREMENT,
  city VARCHAR(64) not null,
  state VARCHAR(8) not null,
  postal_code VARCHAR(32) not null
 )""");
 
 locations.write.mode("append").jdbc(jdbcUrl, "locations", connectionProperties)