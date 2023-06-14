# TIDC_Clock_in
TIDC打卡小程序后端源码

## 登录流程

![image.png](https://i.ibb.co/S3HFSqD/7ed1f985-f20f-4fa5-a346-523afa291e60.png)

## 头像问题

当用户发起注册申请后,管理员审核通过，则用户将可以登录到小程序中。此时，用户的头像是一个默认的头像，点击头像则可以从相册中挑选图片进行上传，作为自己的头像。

**注意**：图片大小不能超过2MB

## 调用接口须知
本项目采用自定义的加密字段，在调用每个接口时，需要传递加密字段（encryption），加密的流程如下：

### 1. 必需参数
接口必须传递两个参数：
- timestamp（时间戳）
- encryption（加密字段）

### 2. 格式化字符串与加密
我们需要将要传递的参数转换为符合要求的字符串，例如我们有如下数据：
```json
{
  "nickname": "微信用户",
  "gender": "男",
  "timestamp":1685261356
}
```
我们需要将其转为为如下格式：
> *nickname=微信用户&gender=男&timestamp=1685261356*

接着,我们调用自定义的MD5加密方法，将字符串进行加密处理，生成一个加密字段，其格式类似于：
> **FIYYMODEUMVIMOGPFUWALXAMMDPYZRCR**

这样，我们就得到了加密字段（encryption），将其传递到后端进行比对校验，方可调用接口。

## 打卡须知

我们调用打卡和退卡的api接口时，应该传递**用户当前的经纬度和当前所连接的WiFi名称**，交给后端进行计算比对。
计算公式如下：

![image.png](https://i.ibb.co/TkBtwtK/Snipaste-2023-06-14-17-57-02.png)

后端实现：

```java
    /**
     * 根据两点的经纬度计算大圆距离
     *
     * @param lat 当前纬度
     * @param lon 当前经度
     * @return 两点之间的距离(米为单位)
     */
    public static double calculateDistance(double lat, double lon) {
        double radCurrentLat = Math.toRadians(lat);
        double radCenterLat = Math.toRadians(centerLat);
        double radCurrentLon = Math.toRadians(lon);
        double radCenterLon = Math.toRadians(centerLon);
        double a = radCurrentLat - radCenterLat; // 两点纬度之差的弧度值
        double b = radCurrentLon - radCenterLon; // 两点经度之差的弧度值
        double s = 2 * Math.asin(
                Math.sqrt(
                        Math.pow(Math.sin(a / 2), 2) + Math.cos(radCurrentLat) * Math.cos(radCenterLat) * Math.pow(Math.sin(b / 2), 2)
                )
        );
        double distance = s * R; // 弧度距离 * 地球半径, 以获取实际距离
        distance = (distance * 10000) / 10;
        return distance; // 返回实际距离的整数值(米)
    }
```

其中，$lat$ 表示当前纬度，$lon$ 表示当前经度，$centerLat$ 和 $centerLon$ 分别是中心点的纬度和经度，$a$ 和 $b$ 分别是两点纬度和经度之差的弧度值，$s$ 表示两点间的大圆距离，其单位是弧度，需要乘上地球半径 R 才能得到实际距离（这里使用的单位是米）。

可以看到，该方法主要通过三角函数计算出了两点之间的大圆距离，其核心是 Haversine 公式。

参考的坐标系标准是 ***国测局02标准（GCJ02**）*
