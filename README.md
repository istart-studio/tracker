# istart.studio.tracker
基于任务运行的跟踪与展示
## spring job 一个类型对应一个job
因为在spring生命周期中，一个Job可能会包含多个Task。
因而使得，task的实例运行在整个JOB中，进而无法从JOB中匹配与之对应的task实例（一致）。
