import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.mapper.ObjectMapper;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;


import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import util.FileLoaderUtil;
import util.TestBase;
import util.TestUtil;
import util.XlsReader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static com.google.gson.JsonParser.*;

public class ProductTest extends TestBase {

    Logger log = Logger.getLogger(ProductTest.class);

    @BeforeMethod
    public void setUp() {
        TestBase.init();
    }

    @DataProvider
    public Object[][] getData() throws IOException, InvalidFormatException {
        Object testData[][] = TestUtil.getDataFromSheet(TestUtil.sheetName_Products);
        log.info("Calling object xlxs");
        return testData;
    }


    @Test(groups="Products", priority = 0)
    public void getProductsTest() throws ParseException {
        Map<String, String> p = new HashMap<String, String>();
        p.put("$limit", "10");
        p.put("$skip", "5");
        RequestSpecification getProducts = RestAssured.given();
        Response response = getProducts.given()
                .baseUri(prop.getProperty("Base_URI")).basePath(prop.getProperty("Base_Path_products"))
                .queryParams(p)
                .when().get();
        Assert.assertEquals(response.getStatusCode(), TestUtil.RESPONSE_CODE_200);
        ResponseBody r = response.getBody();
        System.out.println(r.asString());
        String beauty = r.prettyPrint();
        log.info(r.asString());
        System.out.println(beauty);

    }

    @Test (groups="Products", priority = 1)
    public void getProductsResponseTest() {
        RequestSpecification getProducts = RestAssured.given();
        Response response = getProducts.given()
                .baseUri(prop.getProperty("Base_URI")).basePath(prop.getProperty("Base_Path_products"))
                .param("$limit", "10").param("$skip", "5")
                .when().get();
        String body = response.getBody().asString();
        Assert.assertTrue(body.contains("id"));

    }

    @Test (groups="Products", priority = 2)
    public void postProductsTest() throws IOException {
        String filePath = "src/main/resources/postProductRequests/postProductsPositive.json";
        RequestSpecification postProducts = RestAssured.given();

        //adding headers to request
        Header headerContentType = new Header("Content-Type", "application/json");
        Header headerAccept = new Header("Accept", "application/json");
        List<Header> headerList = new ArrayList<Header>();
        headerList.add(headerContentType);
        headerList.add(headerAccept);
        Headers header = new Headers(headerList);
        postProducts.headers(header);
        //String requestbody = FileLoaderUtil.loadFile(filePath);
        //Add json Body to request
       /* String requestbody= "{\n" +
              "  \"name\": \"Apple\",\n" +
              "  \"type\": \"string\",\n" +
              "  \"price\": 0,\n" +
              "  \"shipping\": 0,\n" +
              "  \"upc\": \"string\",\n" +
              "  \"description\": \"string\",\n" +
              "  \"manufacturer\": \"string\",\n" +
              "  \"model\": \"string\",\n" +
              "  \"url\": \"string\",\n" +
              "  \"image\": \"string\"\n" +
              "}";*/
        postProducts.body(new File(filePath));
        //postProducts.body(requestbody);
        Response postResponse = postProducts.post(prop.getProperty("Base_URI") + prop.getProperty("Base_Path_products"));
        FileWriter responseFile = new FileWriter("src/main/resources/postProductRequests/responseProductsPositive.json");
        responseFile.write(postResponse.getBody().prettyPrint());
        responseFile.close();
        int statuscode = postResponse.getStatusCode();
        System.out.println(statuscode);
        String responseBodyPositive = postResponse.getBody().prettyPrint();
        log.info("postProductsTest : Test Pass");
    }

    @Test (dataProvider = "getData", groups="Products", priority = 3)
    public void postProductsTestWithResponseForDifferentDataSets
            (String name, String type, String price, String shipping, String upc, String description,
             String manufacturer, String model, String url, String image, String response)
            throws IOException {

        RequestSpecification postProducts = RestAssured.given();
        //adding headers to request
        Header headerContentType = new Header("Content-Type", "application/json");
        Header headerAccept = new Header("Accept", "application/json");
        List<Header> headerList = new ArrayList<Header>();
        headerList.add(headerContentType);
        headerList.add(headerAccept);
        Headers header = new Headers(headerList);
        postProducts.headers(header);

        String priceTemp = price.trim().replaceAll("^[\"']+|[\"']+$", "");
        System.out.println(priceTemp);
        String shippingTemp = shipping.trim().replaceAll("^[\"']+|[\"']+$", "");
        System.out.println(shippingTemp);

        int priceValue = (int) (Double.parseDouble(priceTemp));
        System.out.println("Int value" + priceValue);
        int shippingValue = (int) (Double.parseDouble(shippingTemp));
        System.out.println("Int value" + shippingValue);

        //Adding req body
        Map<String, Object> reqParam = new LinkedHashMap<String, Object>();

        reqParam.put("name", name.replace("NULL", ""));
        reqParam.put("type", type.replace("NULL", ""));
        reqParam.put("price", priceValue);
        reqParam.put("shipping", shippingValue);
        reqParam.put("upc", upc.replace("NULL", ""));
        reqParam.put("description", description.replace("NULL", ""));
        reqParam.put("manufacturer", manufacturer.replace("NULL", ""));
        reqParam.put("model", model.replace("NULL", ""));
        reqParam.put("url", url.replace("NULL", ""));
        reqParam.put("image", image.replace("NULL", ""));

        // create a new gson instance
        Gson gson = new Gson();

        String json = gson.toJson(reqParam, LinkedHashMap.class);
        System.out.println(json);
        postProducts.body(json);

        Response responseForDifferentReq = postProducts.post(prop.getProperty("Base_URI") + prop.getProperty("Base_Path_products"));
        int responseValue = responseForDifferentReq.getStatusCode();
        log.info("The response is " + responseValue);

        System.out.println("The response body  is \n " + responseForDifferentReq.body().prettyPrint());
        int responseValueXcel = (int) (Double.parseDouble(response));
        Assert.assertEquals(responseValue, responseValueXcel);
    }

    @Test (groups="Products", priority = 4)
    public void postProductsTestWithResponse() {
        int expResponse[] = new int[]{201, 400, 400};

        String filePathReq = "src/main/resources/ProductsRequest";
        File file = new File(filePathReq);
        File[] files = file.listFiles();

        String filePathResp = "src/main/resources/ProductsResponse/Response.xlsx";
        XlsReader xcelRead = new XlsReader(filePathResp);


        for (int i = 0; i < files.length; i++) {
            RequestSpecification postProducts = RestAssured.given();

            //adding headers to request
            Header headerContentType = new Header("Content-Type", "application/json");
            Header headerAccept = new Header("Accept", "application/json");
            List<Header> headerList = new ArrayList<Header>();
            headerList.add(headerContentType);
            headerList.add(headerAccept);
            Headers header = new Headers(headerList);
            postProducts.headers(header);
            postProducts.body(new File(String.valueOf(files[i])));

            Response r = postProducts.given().when().post(prop.getProperty("Base_URI") + prop.getProperty("Base_Path_products"));
            r.then().assertThat().statusCode(expResponse[i]);

            JsonPath jsonPathEvaluator = r.jsonPath();
            String responseName = jsonPathEvaluator.get("name");
            xcelRead.setCellData("extractedProductsResponse", "name", i + 2, responseName);
            System.out.println(r.getBody().prettyPeek());

        }
    }

    @Test (groups="Products", priority = 5)
    public void getProductsTestById() {
        String requestPath = "src/main/resources/ProductsRequestForGET";
        File f = new File(requestPath);
        File[] filePaths = f.listFiles();

        String filePathResp = "src/main/resources/ProductsResponse/deleteProductId.xlsx";
        XlsReader xcelWrite = new XlsReader(filePathResp);

        String[] names = {"IceApple", "Papaya", "WaterMelon"};

        for (int i = 0; i < filePaths.length; i++) {
            RequestSpecification postProducts = RestAssured.given();
            //adding headers to request
            Header headerContentType = new Header("Content-Type", "application/json");
            Header headerAccept = new Header("Accept", "application/json");
            List<Header> headerList = new ArrayList<Header>();
            headerList.add(headerContentType);
            headerList.add(headerAccept);
            Headers header = new Headers(headerList);
            postProducts.headers(header);
            postProducts.body(new File(String.valueOf(filePaths[i])));

            Response r = postProducts.given().when().post(prop.getProperty("Base_URI") + prop.getProperty("Base_Path_products"));
            r.then().assertThat().statusCode(201);

            JsonPath jsonPathEvaluator = r.jsonPath();
            String responseId = Integer.toString(jsonPathEvaluator.get("id"));
            System.out.println("the response id is  : " + responseId);
            String responseName = jsonPathEvaluator.get("name");
            xcelWrite.setCellData("Product Id Name", "Product Id", i+2,responseId);
            xcelWrite.setCellData("Product Id Name", "Product Name", i+2,responseName);

            System.out.println("getting the product by ID");

            RequestSpecification getById = RestAssured.given();
            Response resGetById = getById.given().header("Accept"," application/json")
                    .when().get(prop.getProperty("Base_URI")+prop.getProperty("Base_Path_products")+"/"+responseId);
            String responseProductById = resGetById.getBody().asString();
            System.out.println(responseProductById);
            Assert.assertTrue(responseProductById.contains(responseId));
            Assert.assertTrue(responseProductById.contains(responseName));
            System.out.println("Test Passed");
     }
    }

    @Test (groups="Products", priority = 6)
    public void deleteProductsTestById()
    {
        String pathOfFile = "src/main/resources/ProductsResponse/deleteProductId.xlsx";
        XlsReader readId = new XlsReader(pathOfFile);

        for(int i=2;i<=4;i++)
        {
            int id = (int)(Double.parseDouble(readId.getCellData("Product Id Name","Product Id", i)));
            System.out.println(id);

            RequestSpecification forDelete =  RestAssured.given();
            try{
                forDelete.when().header("Accept","application/json")
                        .delete(prop.getProperty("Base_URI")+prop.getProperty("Base_Path_products")+"/"+id)
                        .then().assertThat().statusCode(200);
            }catch(Exception e)
            {
                log.info("delete of product id : " +id+ "failed" );
                e.printStackTrace();
            }

     }

    }
}
