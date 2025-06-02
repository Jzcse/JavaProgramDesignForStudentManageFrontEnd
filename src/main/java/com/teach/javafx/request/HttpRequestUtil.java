package com.teach.javafx.request;

import com.teach.javafx.AppStore;
import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.URI;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

/**
 * HttpRequestUtil 后台请求实例程序，主要实践向后台发送请求的方法
 *  static boolean isLocal 业务处理程序实现方式 false java-server实现 前端程序通过下面的方法把数据发送后台程序，后台返回前端需要的数据，true 本地方式 业务处理 在SQLiteJDBC 实现
 *  String serverUrl = "http://localhost:9090" 后台服务的机器地址和端口号
 */
public class HttpRequestUtil {
    private static Gson gson = new Gson();
    private static HttpClient client = HttpClient.newHttpClient();
    public static String serverUrl = "http://localhost:22223";
//    public static String serverUrl = "http://10.27.138.202:22223";
//    public static String serverUrl = "http://202.194.7.29:22222";

    /**
     *  应用关闭是需要做关闭处理
     */
    public static void close(){
    }

    /**
     * String login(LoginRequest request)  用户登录请求实现
     * @param request  username 登录账号 password 登录密码
     * @return  返回null 登录成功 AppStore注册登录账号信息 非空，登录错误信息
     */

    public static String login(LoginRequest request){
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(serverUrl + "/auth/login"))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(request)))
                    .headers("Content-Type", "application/json")
                    .build();
            try {
                HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                System.out.println("response.statusCode===="+response.statusCode());
                if (response.statusCode() == 200) {
                    JwtResponse jwt = gson.fromJson(response.body(), JwtResponse.class);
                    AppStore.setJwt(jwt);
                    return null;
                } else if (response.statusCode() == 401) {
                    return "用户名或密码不存在！";
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "登录失败";
    }

    /**
     * DataResponse request(String url,DataRequest request) 一般数据请求业务的实现
     * @param url  Web请求的Url 对用后的 RequestMapping
     * @param request 请求参数对象
     * @return DataResponse 返回后台返回数据
     */
    public static DataResponse request(String url, DataRequest request){
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(serverUrl + url))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(request)))
                    .headers("Content-Type", "application/json")
                    .headers("Authorization", "Bearer " + AppStore.getJwt().getAccessToken())
                    .build();
            request.add("username",AppStore.getJwt().getUsername());
            HttpClient client = HttpClient.newHttpClient();
            try {
                HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                System.out.println("url=" + url +"    response.statusCode="+response.statusCode());
                if (response.statusCode() == 200) {
                    //                System.out.println(response.body());
                    DataResponse dataResponse = gson.fromJson(response.body(), DataResponse.class);
                    return dataResponse;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        return null;
    }

    /**
     *  MyTreeNode requestTreeNode(String url, DataRequest request) 获取树节点对象
     * @param url  Web请求的Url 对用后的 RequestMapping
     * @param request 请求参数对象
     * @return MyTreeNode 返回后台返回数据
     */
    public static MyTreeNode requestTreeNode(String url, DataRequest request){
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + url))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(request)))
                .headers("Content-Type", "application/json")
                .headers("Authorization", "Bearer "+AppStore.getJwt().getAccessToken())
                .build();
        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpResponse<String>  response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() == 200) {
                return gson.fromJson(response.body(), MyTreeNode.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<MyTreeNode> requestTreeNodeList(String url, DataRequest request){
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + url))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(request)))
                .headers("Content-Type", "application/json")
                .headers("Authorization", "Bearer "+AppStore.getJwt().getAccessToken())
                .build();
        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpResponse<String>  response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() == 200) {
                List list = gson.fromJson(response.body(),List.class);
                List<MyTreeNode> rList = new ArrayList<>();
                for(int i = 0; i < list.size();i++) {
                    rList.add(new MyTreeNode((Map)list.get(i)));
                }
                return rList;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *  List<OptionItem> requestOptionItemList(String url, DataRequest request) 获取OptionItemList对象
     * @param url  Web请求的Url 对用后的 RequestMapping
     * @param request 请求参数对象
     * @return List<OptionItem> 返回后台返回数据
     */
    public static List<OptionItem> requestOptionItemList(String url, DataRequest request){
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + url))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(request)))
                .headers("Content-Type", "application/json")
                .headers("Authorization", "Bearer "+AppStore.getJwt().getAccessToken())
                .build();
        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpResponse<String>  response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() == 200) {
                OptionItemList o = gson.fromJson(response.body(), OptionItemList.class);
                if(o != null)
                return o.getItemList();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *   List<OptionItem> getDictionaryOptionItemList(String code) 获取数据字典OptionItemList对象
     * @param code  数据字典类型吗
     * @param
     * @return List<OptionItem> 返回后台返回数据
     */
    public static  List<OptionItem> getDictionaryOptionItemList(String code) {
        DataRequest req = new DataRequest();
        req.add("code", code);
        return requestOptionItemList("/api/base/getDictionaryOptionItemList",req);
    }

    /**
     *  byte[] requestByteData(String url, DataRequest request) 获取byte[] 对象 下载数据文件等
     * @param url  Web请求的Url 对用后的 RequestMapping
     * @param request 请求参数对象
     * @return List<OptionItem> 返回后台返回数据
     */
    public static byte[] requestByteData(String url, DataRequest request){
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + url))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(request)))
                .headers("Content-Type", "application/json")
                .headers("Authorization", "Bearer "+AppStore.getJwt().getAccessToken())
                .build();
        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpResponse<byte[]>  response = client.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
            if(response.statusCode() == 200) {
                return response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * DataResponse uploadFile(String fileName,String remoteFile) 上传数据文件
     * @param fileName  本地文件名
     * @param remoteFile 远程文件路径
     * @return 上传操作信息
     */
    public static DataResponse uploadFile(String uri,String fileName,String remoteFile)  {
        try {
            Path file = Path.of(fileName);
            HttpClient client = HttpClient.newBuilder().build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(serverUrl+uri+"?uploader=HttpTestApp&remoteFile="+remoteFile + "&fileName="
                            + file.getFileName()))
                    .POST(HttpRequest.BodyPublishers.ofFile(file))
                    .headers("Authorization", "Bearer " + AppStore.getJwt().getAccessToken())
                    .build();
            HttpResponse<String>  response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() == 200) {
                DataResponse dataResponse = gson.fromJson(response.body(), DataResponse.class);
                return dataResponse;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static DataResponse newUploadFile(String url, File file, String paramName, String personId) throws IOException {
        // 直接拼接 serverUrl 和传入的 url
        String URL = serverUrl + url;

        // 构建 multipart/form-data 请求体所需的边界字符串
        String boundary = "----JavaFXBoundary" + UUID.randomUUID();
        ByteArrayOutputStream body = new ByteArrayOutputStream();
        String charset = "UTF-8";

        // 添加 personId 字段
        body.write(("--" + boundary + "\r\n").getBytes(charset));
        body.write(("Content-Disposition: form-data; name=\"personId\"\r\n").getBytes(charset));
        body.write(("\r\n").getBytes(charset));
        body.write((personId + "\r\n").getBytes(charset));

        // 添加文件部分
        body.write(("--" + boundary + "\r\n").getBytes(charset));
        body.write(("Content-Disposition: form-data; name=\"" + paramName + "\"; filename=\"" + file.getName() + "\"\r\n").getBytes(charset));
        body.write(("Content-Type: application/octet-stream\r\n\r\n").getBytes(charset));
        body.write(Files.readAllBytes(file.toPath()));
        body.write(("\r\n--" + boundary + "--\r\n").getBytes(charset));

        // 创建 HttpRequest
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .header("Authorization", "Bearer " + AppStore.getJwt().getAccessToken())
                .POST(HttpRequest.BodyPublishers.ofByteArray(body.toByteArray()))
                .build();

        // 使用 try-with-resources 管理 HttpClient 资源
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return DataResponse.fromJson(response.body());
            } else {
                return new DataResponse(response.statusCode(), "文件上传失败", null);
            }
        } catch (InterruptedException e) {
            // 重新设置中断状态
            Thread.currentThread().interrupt();
            return new DataResponse(500, "请求被中断: " + e.getMessage(), null);
        }
    }

    /**
     *
     * @param url
     * @param file
     * @param paramName
     * @param fileName
     * @return
     * @throws IOException
     */
    public static DataResponse newUploadFile1(String url, File file, String paramName, String fileName, String personId) throws IOException {
        // 直接拼接 serverUrl 和传入的 url
        String URL = serverUrl + url;

        // 创建HttpClient实例
        HttpClient client = HttpClient.newHttpClient();

        // 构建multipart/form-data请求体
        var boundary = new StringBuilder().append("----JavaFXBoundary").append(UUID.randomUUID()).toString();
        var part = new ByteArrayInputStream(Files.readAllBytes(file.toPath()));
        var body = new ByteArrayOutputStream();
        String charset = "UTF-8";

        // 添加 personId 字段
        body.write(("--" + boundary + "\r\n").getBytes(charset));
        body.write(("Content-Disposition: form-data; name=\"personId\"\r\n").getBytes(charset));
        body.write(("\r\n").getBytes(charset));
        body.write((personId + "\r\n").getBytes(charset));

        // 添加文件部分
        body.write(("--" + boundary + "\r\n").getBytes(charset));
        body.write(("Content-Disposition: form-data; name=\"" + paramName + "\"; fileName=\"" + file.getName() + "\"\r\n").getBytes(charset));
        body.write(("Content-Type: application/octet-stream\r\n\r\n").getBytes(charset));
        body.write(part.readAllBytes());
        body.write(("\r\n--" + boundary + "--\r\n").getBytes(charset));

        // 创建HttpRequest
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .header("Authorization", "Bearer " + AppStore.getJwt().getAccessToken()) // 添加认证信息
                .POST(HttpRequest.BodyPublishers.ofByteArray(body.toByteArray()))
                .build();

        // 发送请求并处理响应
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return DataResponse.fromJson(response.body());
            } else {
                return new DataResponse(response.statusCode(), "文件上传失败", null);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new DataResponse(500, "网络异常: " + e.getMessage(), null);
        } catch (InterruptedException e) {
            // 重新设置中断状态
            Thread.currentThread().interrupt();
            e.printStackTrace();
            return new DataResponse(500, "请求被中断: " + e.getMessage(), null);
        }
    }

    /**
     * DataResponse importData(String url, String fileName, String paras) 导入数据文件
     * @param url  Web请求的Url 对用后的 RequestMapping
     * @param fileName 本地文件名
     * @param paras  上传参数
     * @return 导入结果信息
     */
    public static DataResponse importData(String url, String fileName, String paras)  {
        try {
            Path file = Path.of(fileName);
            String urlStr = serverUrl+url+"?uploader=HttpTestApp&fileName=" + file.getFileName() ;
            if(paras != null && paras.length() > 0)
                urlStr += "&"+paras;
            HttpClient client = HttpClient.newBuilder().build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlStr))
                    .POST(HttpRequest.BodyPublishers.ofFile(file))
                    .headers("Authorization", "Bearer " + AppStore.getJwt().getAccessToken())
                    .build();
            HttpResponse<String>  response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() == 200) {
                DataResponse dataResponse = gson.fromJson(response.body(), DataResponse.class);
                return dataResponse;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * DataResponse int uploadHtmlString(String html) 加密上传html模板字符串，用于生成htmp网页和PDF文件
     * @param html 上传的HTML字符串
     * @return html 序列号
     */


}
