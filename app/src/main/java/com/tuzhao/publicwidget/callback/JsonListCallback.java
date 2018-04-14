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
 */

public abstract class JsonListCallback<T,D> extends AbsCallback<T> {

    @Override
    public T convertSuccess(Response response) throws Exception {
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
            Base_Class_List_Info<D> base_class_List_info = Convert.fromJson(jsonReader, type);
            response.close();
            String serverTime = base_class_List_info.time;
            TimeManager.getInstance().initTime(serverTime);
            try {
                int code = Integer.parseInt(base_class_List_info.code);
                //返回数据正确
                if (code == 0) {
                    return (T) base_class_List_info;
                }else{
                    //抛出异常错误码
                    throw new IllegalStateException(String.valueOf(code));
                }
            }catch (NumberFormatException e){
                //抛出code转换异常错误码
                throw new NumberFormatException("900");
            }

        }else if (rawType == Base_Class_Info.class){
            //有数据类型，表示有data
            Base_Class_Info base_info = Convert.fromJson(jsonReader, type);
            response.close();
            String serverTime = base_info.time;
            TimeManager.getInstance().initTime(serverTime);
            try {
                int code = Integer.parseInt(base_info.code);
                //返回数据正确
                if (code == 0) {
                    return (T) base_info;
                }else{
                    //抛出异常错误码
                    throw new IllegalStateException(""+code);
                }
            }catch (NumberFormatException e){
                //抛出异常错误码
                throw new NumberFormatException("900");
            }
        } else {
            //未设置实例的基类
            response.close();
            throw new IllegalStateException("501");
        }
    }

}
