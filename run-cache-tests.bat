@echo off
echo 正在运行社区模块缓存测试...
mvn test -Dtest=CommunityServiceCacheTests -Dspring.profiles.active=test
echo 测试完成。
pause 