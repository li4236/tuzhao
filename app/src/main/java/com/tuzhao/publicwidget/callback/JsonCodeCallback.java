package com.tuzhao.publicwidget.callback;

import com.google.gson.stream.JsonReader;
import com.tianzhili.www.myselfsdk.okgo.callback.AbsCallback;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicmanager.TimeManager;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Response;

/**
 * Created by juncoder on 2018/4/13.
 * <p>
 * 不对code进行处理
 * </p>
 */

public abstract class JsonCodeCallback<T> extends AbsCallback<T> {

    @Override
    public T convertSuccess(Response response) {
        Type genType = getClass().getGenericSuperclass();
        //从上述的类中取出真实的泛型参数，有些类可能有多个泛型，所以是数组
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        //我们的示例代码中，只有一个泛型，所以取出第一个，得到如下结果
        //com.lzy.demo.model.LzyResponse<com.lzy.demo.model.ServerModel>
        Type type = params[0];

        // 这里这么写的原因是，我们需要保证上面我解析到的type泛型，仍然还具有一层参数化的泛型，也就是两层泛型
        // 如果你不喜欢这么写，不喜欢传递两层泛型，那么以下两行代码不用写，并且javabean按照
        // https://github.com/jeasonlzy/okhttp-OkGo/blob/master/README_JSONCALLBACK.md 这里的第一种方式定义就可以实现
        if (!(type instanceof ParameterizedType)) throw new IllegalStateException("没有填写泛型参数");
        //如果确实还有泛型，那么我们需要取出真实的泛型，得到如下结果
        //class com.lzy.demo.model.LzyResponse
        //此时，rawType的类型实际上是 class，但 Class 实现了 Type 接口，所以我们用 Type 接收没有问题
        Type rawType = ((ParameterizedType) type).getRawType();
        //这里获取最终内部泛型的类型 com.lzy.demo.model.ServerModel
        Type typeArgument = ((ParameterizedType) type).getActualTypeArguments()[0];
        //这里我们既然都已经拿到了泛型的真实类型，即对应的 class ，那么当然可以开始解析数据了，我们采用 Gson 解析
        //以下代码是根据泛型解析数据，返回对象，返回的对象自动以参数的形式传递到 onSuccess 中，可以直接使用
        JsonReader jsonReader = new JsonReader(response.body().charStream());
        if (rawType == Base_Class_List_Info.class) {
            //有数据类型，表示有data
            Base_Class_List_Info base_class_List_info = Convert.fromJson(jsonReader, type);
            response.close();
            String serverTime = base_class_List_info.time;
            TimeManager.getInstance().initTime(serverTime);
            return (T) base_class_List_info;
        } else if (rawType == Base_Class_Info.class) {
            //有数据类型，表示有data
            Base_Class_Info base_info = Convert.fromJson(jsonReader, type);
            response.close();
            String serverTime = base_info.time;
            TimeManager.getInstance().initTime(serverTime);
            return (T) base_info;
        } else {
            //未设置实例的基类
            response.close();
            throw new IllegalStateException("501");
        }
    }

}
