import numpy as np                                              # 导入数学函数
from scipy.optimize import curve_fit as curve_fit      # 导入拟合函数

def formateJson(request):
    cases=list(map(int,request.POST['cases'][1:-1].split(',')))
    deaths = list(map(int,request.POST['deaths'][1:-1].split(',')))
    formateList=[]
    formateList.append(cases)
    formateList.append(deaths)
    return formateList

# 计算
def forecastData(dataLists,days):
    cases=dataLists[0]
    deaths=dataLists[1]
    print(cases)
    print(deaths)
    forecastCases=",".join(str(i) for i in getForecastData(cases,days).astype(int))
    forecastDeaths =",".join(str(i) for i in getForecastData(deaths, days).astype(int))
    forecastCases="["+forecastCases+"]"
    forecastDeaths="["+forecastDeaths+"]"

    res=[]
    res.append(forecastCases)
    res.append(forecastDeaths)

    return res

def getForecastData(dataList,days):
    dateScope=len(dataList)
    print(dateScope)
    xdata = [i + 1 for i in range(dateScope)]  # 横坐标数据，以第几天表示
    ydata = dataList  # 纵坐标数据，表示每天对应的病例数

    popt, pcov = curve_fit(func, xdata, ydata, method='dogbox',bounds=([1000., 0.01, 10.], [10000000., 1.0, 1000.]))
    k = popt[0]
    a = popt[1]
    b = popt[2]

    x = np.linspace(0, len(xdata) + days)  # 横坐标取值
    y = func(x, *popt)  # 纵坐标计算值

    return y

def func(x, k, a, b):
    return k/(1+(k/b-1)*np.exp(-a*x))
