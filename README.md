# contentprovider
### 概要
封装了contentprovider的insert update等操作并且包含notifychange 


### 实用说明
1. library包含XjtContentProvider,这个类是contentprovider的实现，同时在使用时作为当前app的provider的基类。
2. XjtContentProvider里面的SQLiteOpenHelper需要自己实现，并在oncreate中注入XjtContentProvider。
3. 不要忘记在AndroidManifest.xml中声明provider。
4. demo种简要展示如何使用
