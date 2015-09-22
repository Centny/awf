HTTP Usage
===
### Callback
the `HCacheCallback` is common in use,like:

```
H.doGet("http://www.xxx.xx?a=1", new HCacheCallback() {
			
  @Override
  public void onSuccess(CBase c, HResp res, String data) throws Exception {
  //do something when load success
  }
			
  @Override
  public void onError(CBase c, String cache, Throwable err) throws Exception    {
  //do something when load error. the cache will not null when cache found.
				
  }
})

```

all call back is extends from `HCallback` interface, it contain all supported callback method.

`note: All callback already method is running on Handler except some process method, it is meaning that you do not need handle the callback on Handler when you want to update the UI component`

### Arguments Builder
you can build the argument list, like:

```
Args.V args=Args.A("a", "1").A("b", 1);
```

### GET

```
H.doGet("http://www.xxx.xx?a=1", <call back>);
H.doGet("http://www.xxx.xx?a=1", <arguments>, <call back>);
```

### POST

```
//normal post, not arguments.
H.doPost(url, callback);
//normal post arguments to server by application/x-www-form-urlencoded.
H.doPost(url, args, callback)
//post for uploading file to server by multipart/form-data.
H.doPost(url, pis, callback)
//post for uploading bitmap to server by multipart/form-data.
H.doPost(url, name, bitmap, cb)
//post data as http body to server
H.doPostData(url, data, cb);
```