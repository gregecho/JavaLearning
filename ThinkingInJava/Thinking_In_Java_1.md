## 正文 ##
### 1. Java访问权限 ###
>+ public: 表示成员对任何人都是可用的。
>+ private: 表示除类型创建者及类型的内部方法之外的任何人都不能访问的元素。
>+ protected: 与private作用相当，差别仅在于继承的类可以访问protected成员，而不能访问private成员。
>+ 包访问权限: java的默认访问权限，当没有使用前三者的情况下，默认使用_包访问权限_ **可以访问包内的其他成员，而不能访问其他包的成员。**

### 2. java中五个存储数据的地方 ###
>+ **寄存器:** 最快的存储区，位于处理器内部。但是寄存器的数量有限，java中不能直接操作寄存器。
>+ **堆栈:** 驻留于常规RAM（随机访问存储器）区域，堆栈指针若向下移，会创建新的内存；若向上移，则会释放那些内存。这是一种特别快、特别有效的数据保存方式，仅次于寄存器。**在java程序中，一般的8种基本类型数据和对象的引用通常存放在栈内存中，不通过new关键字的字符串对象也是存放在栈的字符串池中。栈的优势是，存取速度比堆要快，仅次于寄存器，栈数据可以共享。但缺点是，存在栈中的数据大小与生存期必须是确定的，缺乏灵活性**。
>+ **堆：** 一种常规用途的内存池（也在RAM区域），其中保存了Java对象。和堆栈不同，“内存堆”或“堆”（Heap）不必知道要从堆里分配多少存储空间，也不必知道存储的数据要在堆里停留多长的时间。java的堆是一个运行时数据区，类的对象从中分配空间，**凡是通过new关键字创建的对象都存放在堆内存中，它们不需要程序代码来显式的释放。堆是由垃圾回收来负责的，堆的优势是可以动态地分配内存大小，生存期也不必事先告诉编译器，因为它是在运行时动态分配内存的**，Java的垃圾收集器会自动收走这些不再使用的数据。但缺点是，由于要在运行时动态分配内存，存取速度较慢。
>+ **静态存储:** “静态”（Static）是指“位于固定位置”（尽管也在RAM里）。程序运行期间，静态存储的数据将随时等候调用。可用static关键字指出一个对象的特定元素是静态的。但Java对象本身永远都不会置入静态存储空间。
>+ **常数存储:** 常数值通常直接置于程序代码内部。这样做是安全的，因为它们永远都不会改变。有的常数需要严格地保护，所以可考虑将它们置入只读存储器（ROM）。
>+ **非随机存储器(Non-RAM storage)：** 对于流对象和持久化对象，通常存放在程序外的存储器，如硬盘。

### 3. java中的析构函数 ###
Java中没有像C/C++的析构函数，用来销毁不用的对象是否内存空间，只有以下三个方法用于通知垃圾回收器回收对象。
>+ finalize()只是通知JVM的垃圾收集器当前的对象不再使用可以被回收了，但是垃圾回收器根据内存使用状况来决定是否回收。
finalize()最有用的地方是在JNI调用本地方法时(C/C++方法)，调用本地方法的析构函数消耗对象释放函数。
>+ System.gc()是强制析构，显式通知垃圾回收器释放内存，但是垃圾回收器也不一定会立即执行，垃圾回收器根据当前内存使用状况和对象的生命周期自行决定是否回收。
>+ RunTime.getRunTime().gc()和System.gc()类似。
**注意：这三个函数都不能保证垃圾回收器立即执行，推荐不要频繁使用**

### 4. java垃圾回收原理 ###
#### 4.1 引用计数器 ####
> 一种简单但是速度较慢的垃圾回收算法，每个对象拥有一个引用计数器(Reference Counter)，当每次引用附加到这个对象时，对象的引用计数器加1。当每次引用超出作用范围或者被设置为null时，对象的引用计数器减1。垃圾回收器遍历整个对象列表，当发现一个对象的引用计数器为0时，将该对象移出内存释放。

> **缺点**：引用计数算法的缺点是，当对象环状相互引用时，对象的引用计数器总不为0，要想回收这些对象需要额外的处理。引用计数算法只是用来解释垃圾回收器的工作原理，没有JVM使用它实现垃圾回收器。

#### 4.2 停止-复制（stop-and-copy） ####
> 垃圾回收器的收集机制基于：任何一个存活的对象必须要被一个存储在栈或者静态存储区的引用所引用。
暂停复制的算法是：程序在运行过程中首先暂停执行，把每个存活的对象从一个堆复制到另一个堆中，已经不再被使用的对象被回收而不再复制。
> **缺点**：
>+ 必须要同时维护分离的两个堆，需要程序运行所需两倍的内存空间。JVM的解决办法是在内存块中分配堆空间，复制时简单地从一个内存块复制到另一个内存块。
>+ 复制过程的本身处理，当程序运行稳定以后，只会产生很少的垃圾对象需要回收，如果垃圾回收器还是频繁地复制存活对象是非常低性能的。JVM的解决方法是使用一种新的垃圾回收算法——标记清除(mark-and-sweep)。

#### 4.3 标记清除(mark-and-sweep) ####
> 所依据的思路同样是从堆栈和静态存储区出发，遍历所有引用，进而找出所有存活的对象。当每次找到一个存活的对象时，对象被设置一个标记并且不被回收，当标记过程完成后，清除不用的死对象，释放内存空间。
标记清除算法不需要复制对象，所有的标记和清除工作在一个内存堆中完成。

#### 4.4 分代复制(generation-copy)算法 ####
>+ 一种对暂停复制算法的改进，**JVM分配内存是按_块_分配的**，当创建一个大对象时，需要占用一块内存空间，严格的暂停复制算法在释放老内存堆之前要求把每个存活的对象从源堆拷贝到新堆，这样做非常的消耗内存。有了块之后，垃圾回收器在回收的时候就可以往废弃的块里拷贝对象了。每个块有相应的_代数_(generation count)来记录它是否存活。垃圾回收器会定期进行完整的清理动作，大型对象仍然不会被复制（只是其代数会增加），内含小心对象的内存块则被复制并且整理。
>+ JVM监控垃圾回收器的效率，当发现所有的对象都是长时间存活时，JVM将垃圾回收器的收集算法调整为标记清除，当内存堆变得零散碎片时，JVM又重新将垃圾回收器的算法切换会暂停复制，这就是JVM的自适应分代暂停复制标记清除垃圾回收算法的思想。

### 5.java即时编译技术(JIT) ###
Java的JIT是just-in-timecomplier技术，JIT技术是java代码部分地或全部转换成本地机器码程序，不再需要JVM解释，执行速度更快。当一个”.class”的类文件被找到时，类文件的字节码被调入内存中，这时JIT编译器编译字节码代码。
JIT有两个不足：
>+ JIT编译转换需要花费一些时间，这些时间贯穿于程序的整个生命周期。
>+ JIT增加了可执行代码的size，相比于压缩的字节码，JIT代码扩展了代码的size，这有可能引起内存分页，进而降低程序执行速度。

对JIT不足的一种改进技术是延迟评估(lazy evaluation)：其基本原理是字节码并不立即进行JIT编译除非必要，在最新的JDK中采用了一种类似延迟JIT的HotSpot方法对每次执行的代码进行优化，代码执行次数越多，速度越快。

### 6.成员初始化 ###
> 初始化顺序：在一个类里，初始化的顺序是由变量在类内的定义顺序决定的。即使变量定义大量遍布于方法定义的中间，那些变量仍会在调用任何方法之前得到初始化——甚至在构建器调用之前。
````java
//: OrderOfInitialization.java
// Demonstrates initialization order.

// When the constructor is called, to create a
// Tag object, you'll see a message:
class Tag {
  Tag(int marker) {
    System.out.println("Tag(" + marker + ")");
  }
}

class Card {
  Tag t1 = new Tag(1); // Before constructor
  Card() {
    // Indicate we're in the constructor:
    System.out.println("Card()");
    t3 = new Tag(33); // Re-initialize t3
  }
  Tag t2 = new Tag(2); // After constructor
  void f() {
    System.out.println("f()");
  }
  Tag t3 = new Tag(3); // At end
}

public class OrderOfInitialization {
  public static void main(String[] args) {
    Card t = new Card();
    t.f(); // Shows that construction is done
  }
} ///:~
//Tag(1)
//Tag(2)
//Tag(3)
//Card()
//Tag(33)
//f()
````

#### 6.1静态数据的初始化 ####
若数据是静态的（static），那么同样的事情就会发生；如果它属于一个基本类型（主类型），而且未对其初始化，就会自动获得自己的标准基本类型初始值；如果它是指向一个对象的句柄，那么除非新建一个对象，并将句柄同它连接起来，否则就会得到一个空值（NULL）。
````java
//: StaticInitialization.java
// Specifying initial values in a
// class definition.

class Bowl {
  Bowl(int marker) {
    System.out.println("Bowl(" + marker + ")");
  }
  void f(int marker) {
    System.out.println("f(" + marker + ")");
  }
}

class Table {
  static Bowl b1 = new Bowl(1);
  Table() {
    System.out.println("Table()");
    b2.f(1);
  }
  void f2(int marker) {
    System.out.println("f2(" + marker + ")");
  }
  static Bowl b2 = new Bowl(2);
}

class Cupboard {
  Bowl b3 = new Bowl(3);
  static Bowl b4 = new Bowl(4);
  Cupboard() {
    System.out.println("Cupboard()");
    b4.f(2);
  }
  void f3(int marker) {
    System.out.println("f3(" + marker + ")");
  }
  static Bowl b5 = new Bowl(5);
}

public class StaticInitialization {
  public static void main(String[] args) {
    System.out.println(
      "Creating new Cupboard() in main");
    new Cupboard();
    System.out.println(
      "Creating new Cupboard() in main");
    new Cupboard();
    t2.f2(1);
    t3.f3(1);
  }
  static Table t2 = new Table();
  static Cupboard t3 = new Cupboard();
} ///:~
````
static初始化只有在必要的时候才会进行。如果不创建一个Table对象，而且永远都不引用Table.b1或Table.b2，那么static Bowl b1和b2永远都不会创建。然而，**只有在创建了第一个Table对象之后（或者发生了第一次static访问），它们才会创建。在那以后，static对象不会重新初始化**。 **初始化的顺序是首先static（如果它们尚未由前一次对象创建过程初始化），接着是非static对象。**

在这里有必要总结一下对象的创建过程。请考虑一个名为Dog的类：

(1) 类型为Dog的一个对象首次创建时，或者Dog类的static方法／static字段首次访问时，Java解释器必须找到Dog.class（在事先设好的类路径里搜索）。

(2) 找到Dog.class后（它会创建一个Class对象，这将在后面学到），它的所有static初始化模块都会运行。因此，**static初始化仅发生一次——在Class对象首次载入的时候**。

(3) 创建一个new Dog()时，Dog对象的构建进程首先会在内存堆（Heap）里为一个Dog对象分配足够多的存储空间。

(4) 这种存储空间会清为零，将Dog中的所有基本类型设为它们的默认值（零用于数字，以及boolean和char的等价设定）。

(5) 进行字段定义时发生的所有初始化都会执行。

(6) 执行构建器。正如第6章将要讲到的那样，这实际可能要求进行相当多的操作，特别是在涉及继承的时候。

##### 6.1.1显示的静态初始化 #####
尽管看起来象个方法，但它实际只是一个static关键字，后面跟随一个方法主体。**与其他static初始化一样，这段代码仅执行一次——首次生成那个类的一个对象时，或者首次访问属于那个类的一个static成员时**（即便从未生成过那个类的对象）。
````java
class Cups {
  static Cup c1;
  static Cup c2;
  static {
    c1 = new Cup(1);
    c2 = new Cup(2);
  }
  Cups() {
    System.out.println("Cups()");
  }
}
````

##### 6.1.2非静态实例的初始化 #####
它看起来与静态初始化从句极其相似，只是static关键字从里面消失了。为支持对“匿名内部类”的初始化（参见第7章），必须采用这一语法格式。
````java
public class Mugs {
  Mug c1;
  Mug c2;
  {
    c1 = new Mug(1);
    c2 = new Mug(2);
    System.out.println("c1 & c2 initialized");
  }
  Mugs() {
    System.out.println("Mugs()");
  }
  public static void main(String[] args) {
    System.out.println("Inside main()");
    Mugs x = new Mugs();
  }
}
````
