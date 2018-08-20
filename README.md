# spider
本项目是java语言编写的百度图片爬虫
配置文件:

src/main/resources/application.properties

下载工作入口：

src/main/java/com/shijt/spider/jobs/DownloadJobs

linux编译：
sh package.sh

运行:
编译之后target文件夹下会生成spider.jar文件，将application.properties文件与spider.jar放在同一目录下执行以下命令即可：
java -jar spider.jar
