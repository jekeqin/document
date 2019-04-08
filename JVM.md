# JVM 调优

+ GC 使用情况显示
    + VM arguments 添加：-XX:+PrintGCDetails
    ```
    Heap
      PSYoungGen      total 38400K, used 18660K [0x00000000d5f80000, 0x00000000d8a00000, 0x0000000100000000)
        eden space 33280K, 56% used [0x00000000d5f80000,0x00000000d71b9258,0x00000000d8000000)
        from space 5120K, 0% used [0x00000000d8500000,0x00000000d8500000,0x00000000d8a00000)
        to   space 5120K, 0% used [0x00000000d8000000,0x00000000d8000000,0x00000000d8500000)
      ParOldGen       total 87552K, used 0K [0x0000000081e00000, 0x0000000087380000, 0x00000000d5f80000)
        object space 87552K, 0% used [0x0000000081e00000,0x0000000081e00000,0x0000000087380000)
      Metaspace       used 6624K, capacity 6722K, committed 6784K, reserved 1056768K
        class space    used 801K, capacity 857K, committed 896K, reserved 1048576K
    ```
## GC 区域划分

+ JDK1.8    运行状态堆内存划分
```
Heap                    # 堆
    PSYoungGen          # 新生代
        eden space      # 伊甸空间（伊甸园）
        from space
        to   space
    ParOldGen           # 老年代
        object space
    Metaspace           # 元空间（取代永久代）
        class space
```
+ Heap
    + PSYoungGen
        + eden
            + Eden 区位于Java堆的年轻代，是新对象分配内存的地方，由于堆是所有线程共享的，因此在堆上分配内存需要加锁。而Sun JDK为提升效率，会为每个新建的线程在Eden上分配一块独立的空间由该线程独享，这块空间称为TLAB（Thread Local Allocation Buffer）。在TLAB上分配内存不需要加锁，因此JVM在给线程中的对象分配内存时会尽量在TLAB上分配。如果对象过大或TLAB用完，则仍然在堆上进行分配。如果Eden区内存也用完了，则会进行一次Minor GC（young GC）。
            + 该区域是最主要的刚创建的对象的内存分配区域，绝大多数对象都会被创建到这里（除了部分大对象通过内存担保机制创建到Old区域，默认大对象都是能够存活较长时间的），该区域的对象大部分都是短时间都会死亡的，故垃圾回收器针对该部分主要采用标记整理算法了回收该区域。
        + from/to Survival
            + 针对新生代对象"朝夕生死"的特点,将新生代划分为3块区域u，分别为Eden、From Survior、ToSurvior，比例为8：1：1。
            + Survival区与Eden区相同都在Java堆的年轻代。Survival区有两块，一块称为from区，另一块为to区，这两个区是相对的，在发生一次Minor GC后，from区就会和to区互换。在发生Minor GC时，Eden区和Survivalfrom区会把一些仍然存活的对象复制进Survival to区，并清除内存。Survival to区会把一些存活得足够旧的对象移至年老代。
            + From和To是相对的，每次Eden和From发生Minor GC时,会将存活的对象复制到To区域,并清除内存。To区域内的对象每存活一次，它的"age"就会+1，当达到某个阈值（默认为15）时，To Survior区域内的对象就会被转移到老年代。
            + 可以通过设置参数 -XX:MaxTenuringThreshold 来设置晋升的年龄。
            + 虚拟机提供了一个参数：-XX  PertenureSizeThreshold 使得大于这个参数的对象直接在老年代中分配内存，这样就避免了在Eden区域以及Survior区域进行大量的内存复制。
    + ParOldGen
        + 老年代中是存活时间久的,大小较大的对象(很长的字符串或者是数组)，因此老年代使用标记-整理算法。当老年代容量满的时候，会触发一次MajorGC （FullGC），回收年老代和年轻代中不再被使用的对象资源。
        + 一般能够在Surviver中没有被清除出去的对象才会进入到这块区域.
    + Metaspace
        + JDK8 HotSpot JVM 使用本地内存来存储类元数据信息并称之为：元空间（Metaspace）；这与Oracle JRockit 和IBM JVM’s很相似。这将是一个好消息：意味着不会再有java.lang.OutOfMemoryError: PermGen问题，也不再需要你进行调优及监控内存空间的使用，但是新特性不能消除类和类加载器导致的内存泄漏。你需要使用不同的方法以及遵守新的命名约定来追踪这些问题。
        + 用于描述类元数据的“klasses”已经被移除。
        + 新参数（MaxMetaspaceSize）用于限制本地内存分配给类元数据的大小。如果没有指定这个参数，元空间会在运行时根据需要动态调整。
        + 元空间是方法区的在HotSpot jvm 中的实现，方法区主要用于存储类的信息、常量池、方法数据、方法代码等。方法区逻辑上属于堆的一部分，但是为了与堆进行区分，通常又叫“非堆”。 
        + 元空间的本质和永久代类似，都是对JVM规范中方法区的实现。不过元空间与永久代之间最大的区别在于：元空间并不在虚拟机中，而是使用本地内存。
        + 默认情况下，类元数据只受可用的本地内存限制（容量取决于是32位或是64位操作系统的可用虚拟内存大小）。可见也不是无限制的，需要配置参数。
        + **常用配置参数**
            + -XX:MetaspaceSize
                + 初始化的Metaspace大小，控制元空间发生GC的阈值。GC后，动态增加或降低MetaspaceSize。在默认情况下，这个值大小根据不同的平台在12M到20M浮动。使用Java -XX:+PrintFlagsInitial命令查看本机的初始化参数
            + -XX:MaxMetaspaceSize
                + 限制Metaspace增长的上限，防止因为某些情况导致Metaspace无限的使用本地内存，影响到其他程序。在本机上该参数的默认值为4294967295B（大约4096MB）。
            + -XX:MinMetaspaceFreeRatio
                + 当进行过Metaspace GC之后，会计算当前Metaspace的空闲空间比，如果空闲比小于这个参数（即实际非空闲占比过大，内存不够用），那么虚拟机将增长Metaspace的大小。默认值为40，也就是40%。设置该参数可以控制Metaspace的增长的速度，太小的值会导致Metaspace增长的缓慢，Metaspace的使用逐渐趋于饱和，可能会影响之后类的加载。而太大的值会导致Metaspace增长的过快，浪费内存。
            + -XX:MaxMetasaceFreeRatio
                + 当进行过Metaspace GC之后， 会计算当前Metaspace的空闲空间比，如果空闲比大于这个参数，那么虚拟机会释放Metaspace的部分空间。默认值为70，也就是70%。
            + -XX:MaxMetaspaceExpansion
                + Metaspace增长时的最大幅度。在本机上该参数的默认值为5452592B（大约为5MB）。
            + -XX:MinMetaspaceExpansion
                + Metaspace增长时的最小幅度。在本机上该参数的默认值为340784B（大约330KB为）。


## Java 引用
无论是通过引用计数算法判断对象的引用数量，还是通过可达性分析算法判断对象的引用链是否可达，判定对象是否存活都与“引用”有关。在Java语言中，将引用又分为强引用、软引用、弱引用、虚引用4种，这四种引用强度依次逐渐减弱。
+ 强引用
    + 在程序代码中普遍存在的，类似 Object obj = new Object() 这类引用，只要强引用还存在，垃圾收集器永远不会回收掉被引用的对象。
    + 以前我们使用的大部分引用实际上都是强引用，这是使用最普遍的引用。如果一个对象具有强引用，那就类似于必不可少的生活用品，垃圾回收器绝不会回收它。当内存空间不足，Java虚拟机宁愿抛出OutOfMemoryError错误，使程序异常终止，也不会靠随意回收具有强引用的对象来解决内存不足问题。
+ 软引用(SoftReference)
    + 用来描述一些还有用但并非必须的对象。对于软引用关联着的对象，在系统将要发生内存溢出异常之前，将会把这些对象列进回收范围之中进行第二次回收。如果这次回收后还没有足够的内存，才会抛出内存溢出异常。
    + 如果一个对象只具有软引用，那就类似于可有可物的生活用品。如果内存空间足够，垃圾回收器就不会回收它，如果内存空间不足了，就会回收这些对象的内存。只要垃圾回收器没有回收它，该对象就可以被程序使用。软引用可用来实现内存敏感的高速缓存。
    + 软引用可以和一个引用队列（ReferenceQueue）联合使用，如果软引用所引用的对象被垃圾回收，JAVA虚拟机就会把这个软引用加入到与之关联的引用队列中。
+ 弱引用(WeakReference)
    + 也是用来描述非必需对象的，但是它的强度比软引用更弱一些，被弱引用关联的对象只能生存到下一次垃圾收集发生之前。当垃圾收集器工作时，无论当前内存是否足够，都会回收掉只被弱引用关联的对象。
    + 如果一个对象只具有弱引用，那就类似于可有可物的生活用品。弱引用与软引用的区别在于：只具有弱引用的对象拥有更短暂的生命周期。在垃圾回收器线程扫描它 所管辖的内存区域的过程中，一旦发现了只具有弱引用的对象，不管当前内存空间足够与否，都会回收它的内存。不过，由于垃圾回收器是一个优先级很低的线程， 因此不一定会很快发现那些只具有弱引用的对象。
    + 弱引用可以和一个引用队列（ReferenceQueue）联合使用，如果弱引用所引用的对象被垃圾回收，Java虚拟机就会把这个弱引用加入到与之关联的引用队列中。
+ 虚引用
    + 也叫幽灵引用或幻影引用，是最弱的一种引用关系。一个对象是否有虚引用的存在，完全不会对其生存时间构成影响，也无法通过虚引用来取得一个对象实例。它的作用是能在这个对象被收集器回收时收到一个系统通知。
    + 顾名思义，就是形同虚设，与其他几种引用都不同，虚引用并不会决定对象的生命周期。如果一个对象仅持有虚引用，那么它就和没有任何引用一样，在任何时候都可能被垃圾回收。
    + 虚引用主要用来跟踪对象被垃圾回收的活动。
    + 虚引用与软引用和弱引用的一个区别
        + 虚引用必须和引用队列（ReferenceQueue）联合使用。当垃 圾回收器准备回收一个对象时，如果发现它还有虚引用，就会在回收对象的内存之前，把这个虚引用加入到与之关联的引用队列中。程序可以通过判断引用队列中是 否已经加入了虚引用，来了解被引用的对象是否将要被垃圾回收。程序如果发现某个虚引用已经被加入到引用队列，那么就可以在所引用的对象的内存被回收之前采取必要的行动。

在程序设计中一般很少使用弱引用与虚引用，使用软引用的情况较多，这是因为软引用可以加速JVM对垃圾内存的回收速度，可以维护系统的运行安全，防止内存溢出（OutOfMemory）等问题的产生。

## GC 算法
1. 标记-清除算法（Mark-Sweep）
    + 标记-清除算法采用从根集合（GC Roots）进行扫描，对存活的对象进行标记，标记完毕后，再扫描整个空间中未被标记的对象，进行回收。
    + 算法分为“标记”和“清除”阶段：首先标记出所有需要回收的对象，在标记完成后统一回收所有被标记的对象。它是最基础的收集算法，效率也很高，但是会带来两个明显的问题
        + 效率问题
        + 空间问题（标记清除后会产生大量不连续的碎片）
    + 标记-清除算法不需要进行对象的移动，只需对不存活的对象进行处理，在存活对象比较多的情况下极为高效，但由于标记-清除算法直接回收不存活的对象，因此会造成内存碎片。
    + 如果下次有比较大的对象实例需要在堆上分配较大的内存空间时，可能会出现无法找到足够的连续内存而不得不再次触发垃圾回收。

2. 复制算法(Copying)
    + 为了解决效率问题，“复制”收集算法出现了。它可以将内存分为大小相同的两块，每次使用其中的一块。当这一块的内存使用完后，就将还存活的对象复制到另一块去，然后再把使用的空间一次清理掉。这样就使每次的内存回收都是对内存区间的一半进行回收。
    + 复制算法的提出是为了克服句柄的开销和解决内存碎片的问题。它开始时把堆分成 一个对象面和多个空闲面， 程序从对象面为对象分配空间，当对象满了，基于copying算法的垃圾 收集就从根集合（GC Roots）中扫描活动对象，并将每个 活动对象复制到空闲面(使得活动对象所占的内存之间没有空闲洞)，这样空闲面变成了对象面，原来的对象面变成了空闲面，程序会在新的对象面中分配内存。
    + 此GC算法实际上解决了标记-清除算法带来的“内存碎片化”问题。首先还是先标记处待回收内存和不用回收的内存，下一步将不用回收的内存复制到新的内存区域，这样旧的内存区域就可以全部回收，而新的内存区域则是连续的。它的缺点就是会损失掉部分系统内存，因为你总要腾出一部分内存用于复制。
    + 新的对象实例被创建的时候通常在Eden空间，发生在Eden空间上的GC称为Minor GC，当在新生代发生一次GC后，会将Eden和其中一个Survivor空间的内存复制到另外一个Survivor中，如果反复几次有对象一直存活，此时内存对象将会被移至老年代。可以看到新生代中Eden占了大部分，而两个Survivor实际上占了很小一部分。这是因为大部分的对象被创建过后很快就会被GC（这里也许运用了是二八原则）。

3. 标记-整理算法(Mark-compact)(或称为 标记-压缩算法)
    + 根据老年代的特点特出的一种标记算法，标记-整理算法采用标记-清除算法一样的方式进行对象的标记，但在清除时不同，在回收不存活的对象占用的空间后，会将所有的存活对象往左端空闲空间移动，并更新对应的指针。标记-整理算法是在标记-清除算法的基础上，又进行了对象的移动，因此成本更高，但是却解决了内存碎片的问题。
    + 对于新生代，大部分对象都不会存活，所以在新生代中使用复制算法较为高效，而对于老年代来讲，大部分对象可能会继续存活下去，如果此时还是利用复制算法，效率则会降低。标记-压缩算法首先还是“标记”，标记过后，将不用回收的内存对象压缩到内存一端，此时即可直接清除边界处的内存，这样就能避免复制算法带来的效率问题，同时也能避免内存碎片化的问题。老年代的垃圾回收称为“Major GC”。
4. 分代收集算法
    + 当前虚拟机的垃圾收集都采用分代收集算法，这种算法没有什么新的思想，只是根据对象存活周期的不同将内存分为几块。一般将java堆分为新生代和老年代，这样我们就可以根据各个年代的特点选择合适的垃圾收集算法。
    + 比如在新生代中，每次收集都会有大量对象死去，所以可以选择复制算法，只需要付出少量对象的复制成本就可以完成每次垃圾收集。而老年代的对象存活几率是比较高的，而且没有额外的空间对它进行分配担保，所以我们必须选择“标记-清楚”或“标记-整理”算法进行垃圾收集。
    + 分代收集算法是目前大部分JVM的垃圾收集器采用的算法。它的核心思想是根据对象存活的生命周期将内存划分为若干个不同的区域。一般情况下将堆区划分为老年代（Tenured Generation）和新生代（Young Generation），在堆区之外还有一个代就是永久代（Permanet Generation）。老年代的特点是每次垃圾收集时只有少量对象需要被回收，而新生代的特点是每次垃圾回收时都有大量的对象需要被回收，那么就可以根据不同代的特点采取最适合的收集算法。


## GC 垃圾收集器

1. Serial收集器（复制算法)(串行收集器)
    + -XX:+UseSerialGC
    + Serial（串行）收集器收集器是最基本、历史最悠久的垃圾收集器了。大家看名字就知道这个收集器是一个单线程收集器了。它的 “单线程” 的意义不仅仅意味着它只会使用一条垃圾收集线程去完成垃圾收集工作，更重要的是它在进行垃圾收集工作的时候必须暂停其他所有的工作线程（ “Stop The World” ），直到它收集结束。
    + Serial收集器由于没有线程交互的开销，自然可以获得很高的单线程收集效率。Serial收集器对于运行在Client模式下的虚拟机来说是个不错的选择。

2. Serial Old收集器(标记-整理算法)(串行收集器)
    + 老年代单线程收集器，Serial收集器的老年代版本。
    + 这个收集器的主要意义也是被Client模式下的虚拟机使用。在Server模式下，它主要还有两大用途：一个是在JDK1.5及以前的版本中与Parallel Scanvenge收集器搭配使用，另外一个就是作为CMS收集器的后备预案，在并发收集发生Concurrent Mode Failure的时候使用。

3. ParNew收集器(停止-复制算法)(并行收集器)
    + -XX:+UseParNewGC
    + 新生代收集器，可以认为是Serial收集器的多线程版本,在多核CPU环境下有着比Serial更好的表现。
    + 除了使用多线程进行垃圾收集外，其余行为（控制参数、收集算法、回收策略等等）和Serial收集器完全一样。
    + 新生代采用复制算法，老年代采用标记-整理算法。
    + 它是许多运行在Server模式下的虚拟机的首要选择，除了Serial收集器外，只有它能与CMS收集器配合工作。

4. Parallel Scavenge收集器(停止-复制算法)(并行收集器)
    + -XX:+UseParallelGC
        + 使用Parallel收集器+ 老年代串行
    + -XX:+UseParallelOldGC
        + 使用Parallel收集器+ 老年代并行
    + Parallel是采用复制算法的多线程新生代垃圾回收器，似乎和ParNew收集器有很多的相似的地方。但是Parallel Scanvenge收集器的一个特点是它所关注的目标是吞吐量(Throughput)（高效率的利用CPU）。
    + CMS等垃圾收集器的关注点更多的是用户线程的停顿时间（提高用户体验）。所谓吞吐量就是CPU中用于运行用户代码的时间与CPU总消耗时间的比值。停顿时间越短就越适合需要与用户交互的程序，良好的响应速度能够提升用户的体验；而高吞吐量则可以最高效率地利用CPU时间，尽快地完成程序的运算任务，主要适合在后台运算而不需要太多交互的任务。Parallel Scavenge收集器提供了很多参数供用户找到最合适的停顿时间或最大吞吐量，如果对于收集器运作不太了解的话，手工优化存在的话可以选择把内存管理优化交给虚拟机去完成也是一个不错的选择。
    + 新生代采用复制算法，老年代采用标记-整理算法。

5. Parallel Old收集器(停止-复制算法)(并行收集器)
    + Parallel Scavenge收集器的老年代版本。使用多线程和“标记-整理”算法。在注重吞吐量以及CPU资源的场合，吞吐量优先，都可以优先考虑 Parallel Scavenge收集器和Parallel Old收集器。
    + 这个收集器是在jdk1.6中才开始提供的，在此之前，新生代的Parallel Scavenge收集器一直处于比较尴尬的状态。原因是如果新生代Parallel Scavenge收集器，那么老年代除了Serial Old(PS MarkSweep)收集器外别无选择。由于单线程的老年代Serial Old收集器在服务端应用性能上的”拖累“，即使使用了Parallel Scavenge收集器也未必能在整体应用上获得吞吐量最大化的效果，又因为老年代收集中无法充分利用服务器多CPU的处理能力，在老年代很大而且硬件比较高级的环境中，这种组合的吞吐量甚至还不一定有ParNew加CMS的组合”给力“。直到Parallel Old收集器出现后，”吞吐量优先“收集器终于有了比较名副其实的应用，在注重吞吐量及CPU资源敏感的场合，都可以优先考虑Parallel Scavenge加Parallel Old收集器。
    + -UseParallelGC
        + 虚拟机运行在Server模式下的默认值，打开此开关后，使用Parallel Scavenge + Serial Old的收集器组合进行内存回收。
    + -UseParallelOldGC:
        + 打开此开关后，使用Parallel Scavenge + Parallel Old的收集器组合进行垃圾回收

6. CMS(Concurrent Mark Sweep)收集器（标记-清理算法）(并发收集器)
    + CMS 收集器是一种以获取最短回收停顿时间为目标的收集器。它而非常符合在注重用户体验的应用上使用。
    + CMS 收集器是HotSpot虚拟机第一款真正意义上的并发收集器，它第一次实现了让垃圾收集线程与用户线程（基本上）同时工作。
    + CMS 收集器是一个比较重要的回收器，现在应用非常广泛，我们重点来看一下，CMS一种获取最短回收停顿时间为目标的收集器，这使得它很适合用于和用户交互的业务。从名字(Mark Swep)就可以看出，CMS收集器是基于标记清除算法实现的。它的收集过程分为四个步骤：
        1. 初始标记(initial mark)
            + 暂停所有的其他线程，并记录下直接与root相连的对象，速度很快；
        2. 并发标记(concurrent mark)
            + 同时开启GC和用户线程，用一个闭包结构去记录可达对象。但在这个阶段结束，这个闭包结构并不能保证包含当前所有的可达对象。因为用户线程可能会不断的更新引用域，所以GC线程无法保证可达性分析的实时性。所以这个算法里会跟踪记录这些发生引用更新的地方。
        3. 重新标记(remark)
            + 重新标记阶段就是为了修正并发标记期间因为用户程序继续运行而导致标记产生变动的那一部分对象的标记记录，这个阶段的停顿时间一般会比初始标记阶段的时间稍长，远远比并发标记阶段时间短
        4. 并发清除(concurrent sweep)
            +  开启用户线程，同时GC线程开始对为标记的区域做清扫。
    + 注意初始标记和重新标记还是会stop the world，但是在耗费时间更长的并发标记和并发清除两个阶段都可以和用户进程同时工作。
    + 由于CMS收集器是基于标记清除算法实现的，会导致有大量的空间碎片产生，在为大对象分配内存的时候，往往会出现老年代还有很大的空间剩余，但是无法找到足够大的连续空间来分配当前对象，不得不提前开启一次Full GC。为了解决这个问题，CMS收集器默认提供了一个 -XX:+UseCMSCompactAtFullCollection 收集开关参数（默认就是开启的)，用于在CMS收集器进行FullGC完开启内存碎片的合并整理过程，内存整理的过程是无法并发的，这样内存碎片问题倒是没有了，不过停顿时间不得不变长。
    + 虚拟机设计者还提供了另外一个参数-XX:CMSFullGCsBeforeCompaction参数用于设置执行多少次不压缩的FULL GC后跟着来一次带压缩的（默认值为0，表示每次进入Full GC时都进行碎片整理）。
    + 从它的名字就可以看出它是一款优秀的垃圾收集器，主要优点：并发收集、低停顿。但是它有下面三个明显的缺点：
        + 对CPU资源敏感
        + 无法处理浮动垃圾
        + 它使用的回收算法-“标记-清除”算法会导致收集结束时会有大量空间碎片产生
    + CMS收集器特点
        + 尽可能降低停顿
        + 会影响系统整体吞吐量和性能
            + 比如，在用户线程运行过程中，分一半CPU去做GC，系统性能在GC阶段，反应速度就下降一半
        + 清理不彻底
            + 因为在清理阶段，用户线程还在运行，会产生新的垃圾，无法清理
            + 因为和用户线程一起运行，不能在空间快满时再清理（因为也许在并发GC的期间，用户线程又申请了大量内存，导致内存不够）
    + -XX:CMSInitiatingOccupancyFraction设置触发GC的阈值
    + 如果不幸内存预留空间不够，就会引起concurrent mode failure
        + 一旦 concurrent mode failure产生，将使用串行收集器作为后备。
        + CMS也提供了整理碎片的参数
            + -XX:+UseCMSCompactAtFullCollection
                + Full GC后，进行一次整理
                + 整理过程是独占的，会引起停顿时间变长
            + -XX:+CMSFullGCsBeforeCompaction
                + 设置进行几次Full GC后，进行一次碎片整理
            + -XX:ParallelCMSThreads
                + 设定CMS的线程数量（一般情况约等于可用CPU数量）
    + CMS的提出是想改善GC的停顿时间，在GC过程中的确做到了减少GC时间，但是同样导致产生大量内存碎片，又需要消耗大量时间去整理碎片，从本质上并没有改善时间。
    + 由于CMS收集器现在比较常用，下面我们再额外了解一下CMS算法的几个常用参数：
        + UseCMSInitatingOccupancyOnly：表示只在到达阈值的时候，才进行 CMS 回收。
        + 为了减少第二次暂停的时间，通过-XX:+CMSParallelRemarkEnabled开启并行remark。
        + 如果ramark时间还是过长的话，可以开启-XX:+CMSScavengeBeforeRemark选项，强制remark之前开启一次minor gc，减少remark的暂停时间，但是在remark之后也立即开始一次minor gc。
        + CMS默认启动的回收线程数目是(ParallelGCThreads + 3)/4，如果你需要明确设定，可以通过-XX:+ParallelCMSThreads来设定，其中-XX:+ParallelGCThreads代表的年轻代的并发收集线程数目。
        + CMSClassUnloadingEnabled： 允许对类元数据进行回收。
        + CMSInitatingPermOccupancyFraction：当永久区占用率达到这一百分比后，启动 CMS 回收 (前提是-XX:+CMSClassUnloadingEnabled 激活了)。
        + CMSIncrementalMode：使用增量模式，比较适合单 CPU。
        + UseCMSCompactAtFullCollection参数可以使 CMS 在垃圾收集完成后，进行一次内存碎片整理。内存碎片的整理并不是并发进行的。
        + UseFullGCsBeforeCompaction：设定进行多少次 CMS 垃圾回收后，进行一次内存压缩。

7. G1收集器
    + G1是目前技术发展的最前沿成果之一，HotSpot开发团队赋予它的使命是未来可以替换掉JDK1.5中发布的CMS收集器。
    + 和CMS类似，G1收集器收集老年代对象会有短暂停顿。
    + G1收集器是一款面向服务端应用的垃圾收集器。HotSpot团队赋予它的使命是在未来替换掉JDK1.5中发布的CMS收集器。与其他GC收集器相比，G1具备如下特点：
        1. 并行与并发
            + G1能更充分的利用CPU，多核环境下的硬件优势来缩短stop the world的停顿时间。
            + 部分其他收集器原本需要停顿Java线程执行的GC动作，G1收集器仍然可以通过并发的方式让java程序继续执行。
        2. 分代收集
            + 和其他收集器一样，分代的概念在G1中依然存在，不过G1不需要其他的垃圾回收器的配合就可以独自管理整个GC堆。
            + 虽然G1可以不需要其他收集器配合就能独立管理整个GC堆，但是还是保留了分代的概念。
        3. 空间整合
            + G1收集器有利于程序长时间运行，分配大对象时不会无法得到连续的空间而提前触发一次GC。
            + 与CMS的“标记–清理”算法不同，G1从整体来看是基于“标记整理”算法实现的收集器；从局部上来看是基于“复制”算法实现的。
        4. 可预测的非停顿
            + 这是G1相对于CMS的另一大优势，降低停顿时间是G1和CMS共同的关注点，能让使用者明确指定在一个长度为M毫秒的时间片段内，消耗在垃圾收集上的时间不得超过N毫秒。
    + G1收集器的运作大致分为以下几个步骤：
        + 初始标记
            + 标记阶段，首先初始标记(Initial-Mark),这个阶段是停顿的(Stop the World Event)，并且会触发一次普通Mintor GC。对应GC log:GC pause (young) (inital-mark)
            + Root Region Scanning，程序运行过程中会回收survivor区(存活到老年代)，这一过程必须在young GC之前完成。
        + 并发标记
            + Concurrent Marking，在整个堆中进行并发标记(和应用程序并发执行)，此过程可能被young GC中断。在并发标记阶段，若发现区域对象中的所有对象都是垃圾，那个这个区域会被立即回收(图中打X)。同时，并发标记过程中，会计算每个区域的对象活性(区域中存活对象的比例)。
        + 最终标记
            + Remark, 再标记，会有短暂停顿(STW)。再标记阶段是用来收集 并发标记阶段 产生新的垃圾(并发阶段和应用程序一同运行)；G1中采用了比CMS更快的初始快照算法:snapshot-at-the-beginning (SATB)。
        + 筛选回收
            + Copy/Clean up，多线程清除失活对象，会有STW。G1将回收区域的存活对象拷贝到新区域，清除Remember Sets，并发清空回收区域并把它返回到空闲区域链表中。
    + G1收集器在后台维护了一个优先列表，每次根据允许的收集时间，优先选择回收价值最大的Region(这也就是它的名字Garbage-First的由来)。这种使用Region划分内存空间以及有优先级的区域回收方式，保证了GF收集器在有限时间内可以尽可能高的收集效率（把内存化整为零）。


+ 并行（Parallel）
    + 指多条垃圾收集线程并行工作，但此时用户线程仍然处于等待状态。 
    + 并行收集器使用某种传统的算法并使用多线程并行的执行它们的工作。在多cpu机器上使用多线程技术可以显著的提高java应用程序的可扩展性。
+ 并发（Concurrent）
    + 指用户线程与垃圾收集线程同时执行（但不一定是并行，可能会交替执行），用户程序在继续运行，而垃圾收集器运行在另一个CPU上。
    + 并发收集器与应用程序同时运行。这些收集器在某点上（比如压缩时）一般都不得不停止其他操作以完成特定的任务，但是因为其他应用程序可进行其他的后台操作，所以中断其他处理的实际时间大大降低。


## GC 回收触发
由于对象进行了分代处理，因此垃圾回收区域、时间也不一样。GC有两种类型：Scavenge GC和Full GC。
+ Scavenge GC
    + 一般情况下，当新对象生成，并且在Eden申请空间失败时，就会触发Scavenge GC，对Eden区域进行GC，清除非存活对象，并且把尚且存活的对象移动到Survivor区。然后整理Survivor的两个区。这种方式的GC是对年轻代的Eden区进行，不会影响到年老代。因为大部分对象都是从Eden区开始的，同时Eden区不会分配的很大，所以Eden区的GC会频繁进行。因而，一般在这里需要使用速度快、效率高的算法，使Eden去能尽快空闲出来。
+ Full GC
    + 对整个堆进行整理，包括Young、Tenured和Perm。Full GC因为需要对整个堆进行回收，所以比Scavenge GC要慢，因此应该尽可能减少Full GC的次数。在对JVM调优的过程中，很大一部分工作就是对于Full GC的调节。有如下原因可能导致Full GC
        1. 年老代（Tenured）被写满2
        2. 持久代（Perm）被写满
        3. System.gc()被显示调用
        4. 上一次GC之后Heap的各域分配策略动态变化


## GC 常用收集器
+ Serial
+ ParNew + CMS
+ ParallelYoung + ParallelOld
+ G1GC

## JVM 相关参数的含义
+ JVM堆内存相关参数的含义

    参数名称|含义|默认值|备注
    :---|:---|:---|:---
    -Xms|初始堆大小|物理内存的1/64(<1GB)|默认(MinHeapFreeRatio参数可以调整)空余堆内存小于40%时，JVM就会增大堆直到-Xmx的最大限制.
    -Xmx|最大堆大小|物理内存的1/4(<1GB)|默认(MaxHeapFreeRatio参数可以调整)空余堆内存大于70%时，JVM会减少堆直到 -Xms的最小限制
    -Xmn|年轻代大小(1.4or lator)||注意：此处的大小是（eden+ 2 survivor space).与jmap -heap中显示的New gen是不同的。整个堆大小=年轻代大小 + 年老代大小 + 持久代大小.增大年轻代后,将会减小年老代大小.此值对系统性能影响较大,Sun官方推荐配置为整个堆的3/8
    ~~*-XX:NewSize*~~|~~*设置年轻代大小(for 1.3/1.4)*~~
    ~~*-XX:MaxNewSize*~~|~~*年轻代最大值(for 1.3/1.4)*~~
    -XX:PermSize|设置持久代(perm gen)初始值|物理内存的1/64
    -XX:MaxPermSize|设置持久代最大值|物理内存的1/4
    -Xss|每个线程的堆栈大小||JDK5.0以后每个线程堆栈大小为1M,以前每个线程堆栈大小为256K.更具应用的线程所需内存大小进行 调整.在相同物理内存下,减小这个值能生成更多的线程.但是操作系统对一个进程内的线程数还是有限制的,不能无限生成,经验值在3000~5000左右一般小的应用， 如果栈不是很深， 应该是128k够用的 大的应用建议使用256k。这个选项对性能影响比较大，需要严格的测试。（校长）和threadstacksize选项解释很类似,官方文档似乎没有解释,在论坛中有这样一句话:”-Xss is translated in a VM flag named ThreadStackSize”一般设置这个值就可以了。
    -XX:ThreadStackSize|Thread Stack Size||(0 means use default stack size) [Sparc: 512; Solaris x86: 320 (was 256 prior in 5.0 and earlier); Sparc 64 bit: 1024; Linux amd64: 1024 (was 0 in 5.0 and earlier); all others 0.]
    -XX:NewRatio|年轻代(包括Eden和两个Survivor区)与年老代的比值(除去持久代)||-XX:NewRatio=4表示年轻代与年老代所占比值为1:4,年轻代占整个堆栈的1/5,Xms=Xmx并且设置了Xmn的情况下，该参数不需要进行设置。
    -XX:SurvivorRatio|Eden区与Survivor区的大小比值||设置为8,则两个Survivor区与一个Eden区的比值为2:8,一个Survivor区占整个年轻代的1/10
    -XX:LargePageSizeInBytes|内存页的大小|=128m|不可设置过大，会影响Perm的大小
    -XX:+UseFastAccessorMethods|原始类型的快速优化
    -XX:+DisableExplicitGC|关闭System.gc()||这个参数需要严格的测试
    *-XX:MaxTenuringThreshold*|垃圾最大年龄||如果设置为0的话,则年轻代对象不经过Survivor区,直接进入年老代. 对于年老代比较多的应用,可以提高效率.如果将此值设置为一个较大值,则年轻代对象会在Survivor区进行多次复制,这样可以增加对象再年轻代的存活 时间,增加在年轻代即被回收的概率.*该参数只有在串行GC时才有效.*
    -XX:+AggressiveOpts|加快编译
    -XX:+UseBiasedLocking|锁机制的性能改善
    -Xnoclassgc|*禁用类垃圾回收*||*控制永久带的类回收*
    -XX:SoftRefLRUPolicyMSPerMB|每兆堆空闲空间中SoftReference的存活时间|1s|softly reachable objects will remain alive for some amount of time after the last time they were referenced. The default value is one second of lifetime per free megabyte in the heap
    *-XX:PretenureSizeThreshold*|对象超过多大是直接在旧生代分配|0|单位字节 新生代采用Parallel Scavenge GC时无效,另一种直接在旧生代分配的情况是大的数组对象,且数组中无外部引用对象.
    -XX:TLABWasteTargetPercent|TLAB占eden区的百分比|1%
    -XX:+CollectGen0First|FullGC时是否先YGC|false

+ 并行收集器相关参数

    参数名称|含义|默认值|备注
    :---|:---|:---|:---
    -XX:+UseParallelGC|年轻带使用ps||选择垃圾收集器为并行收集器.此配置仅对年轻代有效.即上述配置下,年轻代使用并发收集,而年老代仍旧使用串行收集.(*java7后配置该项后老年代会采用UseParallelOldGC*)
    -XX:+UseParNewGC|设置年轻代为并行收集|可与CMS收集同时使用,JDK5.0以上,JVM会根据系统配置自行设置,所以无需再设置此值
    *-XX:ParallelGCThreads*|并行收集器的线程数||*此值最好配置与处理器数目相等,同样适用于CMS*
    -XX:+UseParallelOldGC|年老代垃圾收集方式为并行收集(Parallel Compacting)||这个是JAVA 6出现的参数选项。*配置该项年轻代也会采用ps*
    -XX:MaxGCPauseMillis|每次年轻代垃圾回收的最长时间(最大暂停时间)||如果无法满足此时间,JVM会自动调整年轻代大小,以满足此值.
    *-XX:+UseAdaptiveSizePolicy*|自动选择年轻代区大小和相应的Survivor区比例|true|设置此选项后,并行收集器会自动选择年轻代区大小和相应的Survivor区比例,以达到目标系统规定的最低相应时间或者收集频率等,*此值建议使用并行收集器时,一直打开.*
    -XX:GCTimeRatio|设置垃圾回收时间占程序运行时间的百分比||公式为1/(1+n)
    -XX:+ScavengeBeforeFullGC|Full GC前调用YGC|true|Do young generation GC prior to a full GC. (Introduced in 1.4.1.)

+ CMS相关参数

    参数名称|含义|默认值|备注
    :---|:---|:---|:---
    -XX:+UseConcMarkSweepGC|使用CMS内存收集||设置该项后，年轻代会自动开启XX：UseParNewGC
    -XX:+AggressiveHeap|||试图是使用大量的物理内存,长时间大内存使用的优化，能检查计算资源（内存， 处理器数量）,至少需要256MB内存,大量的CPU／内存，（在1.4.1在4CPU的机器上已经显示有提升）
    *-XX:ConcGCThreads*|线程数||定义并发CMS过程运行时的线程数（早期JVM版本也叫-XX:ParallelCMSThreads）；如果还标志未设置，JVM会根据并行收集器中的-XX：ParallelGCThreads参数的值来计算出默认的并行CMS线程数，该公式是ConcGCThreads = (ParallelGCThreads + 3)/4。
    -XX:CMSFullGCsBeforeCompaction|多少次后进行内存压缩||由于并发收集器不对内存空间进行压缩,整理,所以运行一段时间以后会产生"碎片",使得运行效率降低.此值设置运行多少次GC以后对内存空间进行压缩,整理.
    *-XX:+CMSParallelRemarkEnabled*|降低标记停顿
    *-XX+UseCMSCompactAtFullCollection*|在FULL GC的时候， 对年老代的压缩||CMS是不会移动内存的， 因此， 这个非常容易产生碎片， 导致内存不够用， 因此， 内存的压缩这个时候就会被启用。 增加这个参数是个好习惯。可能会影响性能,但是可以消除碎片
    -XX:+UseCMSInitiatingOccupancyOnly|使用手动定义初始化定义开始CMS收集||*命令JVM不基于运行时收集的数据来启动CMS垃圾收集周期。*当该标志被开启时，JVM通过CMSInitiatingOccupancyFraction的值进行每一次CMS收集，而不仅仅是第一次。然而，请记住大多数情况下，JVM比我们自己能作出更好的垃圾收集决策。因此，只有当我们充足的理由(比如测试)并且对应用程序产生的对象的生命周期有深刻的认知时，才应该使用该标志。
    *-XX:CMSInitiatingOccupancyFraction*|使用cms作为垃圾回收使用70％后开始CMS收集|92|1.对于并行收集器，当堆满之后便开始进行垃圾收集；但对CMS收集器，长时间等待是不可取的，因为在并发垃圾收集期间应用持续在运行(并且分配对象)。因此，为了在应用程序使用完内存之前完成垃圾收集周期，CMS收集器要比并行收集器更先启动。*因为不同的应用会有不同对象分配模式，JVM会收集实际的对象分配(和释放)的运行时数据，并且分析这些数据，来决定什么时候启动一次CMS垃圾收集周期。为了引导这一过程， JVM会在一开始执行CMS周期前作一些线索查找。*该线索由 -XX:CMSInitiatingOccupancyFraction=value来设置，该值代表老年代堆空间的使用率。比如，value=75意味着第一次CMS垃圾收集会在老年代被占用75%时被触发。通常CMSInitiatingOccupancyFraction的默认值为68(之前很长时间的经历来决定的)。2.为了保证不出现promotion failed（可以使用下面的公式）
    -XX:CMSInitiatingPermOccupancyFraction|设置Perm Gen使用到达多少比率时触发|92
    -XX:+CMSIncrementalMode|设置为增量模式||用于单CPU情况
    -XX:+CMSClassUnloadingEnabled|||*相对于并行收集器，CMS收集器默认不会对永久代进行垃圾回收。*如果希望对永久代进行垃圾回收，可用设置标志-XX:+CMSClassUnloadingEnabled。在早期JVM版本中，要求设置额外的标志-XX:+CMSPermGenSweepingEnabled。注意，即使没有设置这个标志，一旦永久代耗尽空间也会尝试进行垃圾回收，但是收集不会是并行的，而再一次进行Full GC。
    + 公式
        >上面介绍了promontion faild产生的原因是EDEN空间不足的情况下将EDEN与From survivor中的存活对象存入Tosurvivor区时,To survivor区的空间不足，再次晋升到old gen区，而old gen区内存也不够的情况下产生了promontionfaild，从而导致full gc。那可以推断出：eden+from survivor < oldgen区剩余内存时，不会出现promontion faild的情况，**即：**\
        (Xmx-Xmn)*(1-CMSInitiatingOccupancyFraction/100)>=(Xmn-Xmn/(SurvivorRatior+2))\
        >**进而推断出：**\
        CMSInitiatingOccupancyFraction<=((Xmx-Xmn)-(Xmn-Xmn/(SurvivorRatior+2)))/(Xmx-Xmn)*100\
        >**例如：**\
        >当xmx=128xmn=36 SurvivorRatior=1时CMSInitiatingOccupancyFraction<=((128.0-36)-(36-36/(1+2)))/(128-36)*100=73.913\
        >CMSInitiatingOccupancyFraction低于70%需要调整xmn或SurvivorRatior值。
+ 辅助信息

    参数名称|含义|备注
    :---|:---|:---
    -XX:+PrintGC||输出形式:<br/>[GC 118250K->113543K(130112K), 0.0094143 secs]<br/>[Full GC 121376K->10414K(130112K), 0.0650971 secs]
    -XX:+PrintGCDetails||输出形式：<br/>Heap<br/> PSYoungGen      total 38400K, used 18660K [0x00000000d5f80000, 0x00000000d8a00000, 0x0000000100000000)<br/>  eden space 33280K, 56% used [0x00000000d5f80000,0x00000000d71b9258,0x00000000d8000000)<br/>  from space 5120K, 0% used [0x00000000d8500000,0x00000000d8500000,0x00000000d8a00000)<br/>  to   space 5120K, 0% used [0x00000000d8000000,0x00000000d8000000,0x00000000d8500000)<br/> ParOldGen       total 87552K, used 0K [0x0000000081e00000, 0x0000000087380000, 0x00000000d5f80000)<br/>  object space 87552K, 0% used [0x0000000081e00000,0x0000000081e00000,0x0000000087380000)<br/> Metaspace       used 6624K, capacity 6722K, committed 6784K, reserved 1056768K<br/>  class space    used 801K, capacity 857K, committed 896K, reserved 1048576K
    -XX:+PrintGCTimeStamps
    -XX:+PrintGC:PrintGCTimeStamps||可与-XX:+PrintGC -XX:+PrintGCDetails混合使用<br/>输出形式:11.851: [GC 98328K->93620K(130112K), 0.0082960 secs]
    -XX:+PrintGCApplicationStoppedTime|打印垃圾回收期间程序暂停的时间.可与上面混合使用|输出形式:Total time for which application threads were stopped: 0.0468229 seconds
    -XX:+PrintGCApplicationConcurrentTime|打印每次垃圾回收前,程序未中断的执行时间.可与上面混合使|输出形式:Application time: 0.5291524 seconds
    -XX:+PrintHeapAtGC|打印GC前后的详细堆栈信息
    -Xloggc:filename|把相关日志信息记录到文件以便分析.与上面几个配合使用
    -XX:+PrintClassHistogram|garbage collects before printing the histogram.
    -XX:+PrintTLAB|查看TLAB空间的使用情况
    XX:+PrintTenuringDistribution|查看每次minor GC后新的存活周期的阈值| Desired survivor size 1048576 bytes, new threshold 7 (max 15),new threshold 7即标识新的存活周期的阈值为7。

## 备注
+ Xmx和Xms设置一样大，MaxPermSize和MinPermSize设置一样大，这样可以减轻伸缩堆大小带来的压力；
+ 对于响应优先的系统：优先使用CMS收集器；对于CMS，可以设置一个小的年轻代（经验值是128M－256M）和大的老年代，这样能保证系统低延迟的吞吐效率。因为年老大用的是并发回收，即使时间长点也不会影响其他程序继续运行，网站不会停顿。
+ 吞吐量优先的应用:一般吞吐量优先的应用都有一个很大的年轻代和一个较小的年老代。原因是,这样可以尽可能回收掉大部分短期对象,减少中期的对象,而年老代尽存放长期存活对象。

## 参考
[赶路人儿       https://blog.csdn.net/liuxiao723846/article/details/72811810](https://blog.csdn.net/liuxiao723846/article/details/72811810)\
[假装我很酷    https://blog.csdn.net/weixin_30300689/article/details/79888642](https://blog.csdn.net/weixin_30300689/article/details/79888642)\
[只会一点java https://www.cnblogs.com/dennyzhangdd/p/6770188.html](https://www.cnblogs.com/dennyzhangdd/p/6770188.html)\
[Lee_Tech   https://blog.csdn.net/lk7688535/article/details/51767460](https://blog.csdn.net/lk7688535/article/details/51767460)\
[OKevin https://www.cnblogs.com/yulinfeng/p/7163052.html](https://www.cnblogs.com/yulinfeng/p/7163052.html)\
[SnailClimb在csdn https://blog.csdn.net/qq_34337272/article/details/82177383](https://blog.csdn.net/qq_34337272/article/details/82177383)\
[aspirant https://www.cnblogs.com/aspirant/p/8662690.html](https://www.cnblogs.com/aspirant/p/8662690.html)\
[少年天团 http://www.cnblogs.com/1024Community/p/honery.html](http://www.cnblogs.com/1024Community/p/honery.html)