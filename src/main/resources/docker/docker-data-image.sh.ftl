<#--

    THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
    FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.

-->
<#include "/docker/setup-docker.ftl">
cp ${deployed.file.path} docker/volume
echo "....."
find .
echo "....."

cat docker/volume/Dockerfile

echo "Docker Build Data Image"
echo docker build -t ${deployed.name} docker/volume
docker build -t ${deployed.name} docker/volume

