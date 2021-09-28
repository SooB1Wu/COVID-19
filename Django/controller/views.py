from django.http import HttpResponse
from service import services

#接收post请求后处理数据
def getForecastData(request):
    print(request.POST)
    formateList=services.formateJson(request)#格式化为整形数组
    print(formateList[0])
    print(formateList[1])
    res=services.forecastData(formateList,30)#延长30天并预测数据

    return HttpResponse(res)


