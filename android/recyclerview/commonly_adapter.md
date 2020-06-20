# CommonlyAdapter 的诞生过程

##前言
相信很多人刚开始使用`RecyclerView`这个控件的时候会觉得特别方便、适用，
用多了过后都会发现这个每次使用都会写特别多重复的代码。哪怕只是一个简单的使用也需要去适配
几个方法特别的不方便，不是有句话叫做，`程序员`都是`懒人`吗？能一行解决的问题都不希望写两行，
抱着这个目标就有了`CommonlyAdapter`这个库，现在在这个说说这个库的简单实现过程吧！

## 目标

##### 1、使用简单
最好能一行代码就解决问题，身为一个特别`懒`的程序员，我们不希望写太多代码。嗯，就朝着个目标去实现吧！`终极目标`！！！！`懒`！！！

##### 2、易于扩展
在第一点的基础上我们还希望这个框架能够特别容易扩展，不能把我们以前幸幸苦苦写的代码应为产品的一个小小改动就推翻掉不能重复利用。
这是我们特别不希望看见的东西。所以要方便扩展，毕竟我们都是很`懒`的程序员嘛!

##### 3、代码复用
身为一个爱偷`懒`的程序员，我们怎么能写了的代码只能在一个地方用呢？这不是不符合我们的宗旨吗？所以我们写出来的代码最好是能在项目中重复的使用。

##### 4、适配java8的lambda语法
java8都推出这么多年了在Android上到现在还没有完全推广开来，而且lambda语法相比于Android的Callback还是要方便的多，
所以写出来的代码必须要支持lambda语法，比较这也是一种偷`懒`的方式只一嘛！

##### 5、引进java流行的链式编程
我还希望能直接引进java流行的链式编程，比较每次掉一个方法都需要写对象名，这种方式是我们身为一个爱偷`懒`程序员能接受的吗？
不知道你们能不能接受，我反正是有点接受不了，所以必须要实现链式编程，方便我们偷`懒`。

## 终极目标 ！！！`懒`！！！

##所以我们最终要实现的目标

```java_holder_method_tree
    CommonlyAdapter.<String>of()
        .data("1", "2", "3", "4", "5", "6", "7")
        .createItemViews(R.layout.item_test)
        .dataBinds((holder, data) -> holder.provide().setText(R.id.tv_test, data))
        .bind(ViewFinders.cacheViewFinder(this), R.id.rv_test);
```
这个看着好像挺难的，不要急，我们慢慢来，一步一步慢慢实现。

## 实现过程
### 1、[第一步，优化代码结构](commonly_adapter_no_1.md)
### 2、[第二步，分离回调，解决臃肿](commonly_adapter_no_2.md)
### 3、[第三步，使用链式编程和lambda语法使调用更优美](commonly_adapter_no_3.md)
### 4、[第四步，监听更多的回调，实现跟丰富的功能](commonly_adapter_no_4.md)
### 5、[第五步，我本就很累，为什么还需要做这么多事？ Configurator 的到来，为我分担任务](commonly_adapter_no_5.md)
### 6、[第六步，插件的出现，终于可以不用在重复编写代码了](commonly_adapter_no_6.md)

# 加油！！！努力的人最帅！
# link
[作者](https://github.com/j1046697411)  
[CommonlyAdapter 实现过程中的源码](https://github.com/j1046697411/blogs/tree/master/android/CommonlyAdapterBlogs)  