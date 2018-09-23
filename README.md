# streak

[![Build Status](https://secure.travis-ci.org/realityforge-experiments/streak.png?branch=master)](http://travis-ci.org/realityforge-experiments/streak)
[<img src="https://img.shields.io/maven-central/v/org.realityforge.streak/streak.svg?label=latest%20release"/>](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.realityforge.streak%22%20a%3A%22streak%22)

Streak contains some experiments with reactive streaming code.

## Links

* ReactiveX Scheduler: http://reactivex.io/documentation/scheduler.html
* ReactiveX Operators: http://reactivex.io/documentation/operators.html
* Reactive Streams Specification: https://github.com/reactive-streams/reactive-streams-jvm
* Reactive Streams Simple unicast implementation: https://github.com/reactive-streams/reactive-streams-jvm/tree/v1.0.2/examples/src/main/java/org/reactivestreams/example/unicast
* A basic js implementation: https://github.com/zenparsing/zen-observable
* RxJava implementation: https://github.com/ReactiveX/RxJava
* Reactor Overview - reactor another implementation: https://www.infoq.com/articles/reactor-by-example

## To Implement

Must have publishers:
- [ ] `Interval(Duration)` - Every N time frame produce a value (monotonically increasing counter?)
- [ ] `fromIterable(Iterable)` - From an iterable
- [ ] `fromCollection(Collection)` - A collection.

Must have processors:

**Filtering Processors** (Remove items from stream)

- [x] `Filter(Predicate)` - only pass next value if predicate returns true
- [x] `Take(Count)` - take first "count" items (then unsubscribe from source?)
- [x] `First` == `Take(1)`
- [x] `Skip(Count)` - filter out the first "count" items
- [x] `SkipUntil(Predicate)` - filter out all items until predicate returns true the first time.
- [ ] `TaskLast(Count)` -- take last "count" items. i.e. Wait to onComplete and send last Count items. Needs a buffer `Count` long.
- [ ] `Last` == `TaskLast(1)`

**Transformation Processors** (Take items from one stream and transform them)

- [ ] `flatMap(Function<Publisher[]>)` - given one input, produce zero or more publishers. The items from publishers are flattened into source stream.
- [x] `map` - convert value from one type to another

**Combination Processors** (Take 2 or more streams and combine) (a.k.a vertical merging operations as it combines values across streams)

- [ ] `append(Publishers)` - for each publisher wait till it produces onComplete, elide that signal and then
                       subscribe to next. (a.k.a `concat`)
- [ ] `prepend(Publishers)` == `append(reverse(Publishers))`
- [ ] `startWith(value)` == `prepend(of(value))`
- [ ] `merge(Publishers)` (a.k.a. `or(Publishers)`) - for each stream if it produces a value then pass on value. onComplete if all onComplete, onError if any onError
- [ ] `combineLatest(Publishers)` - for each stream grab latest value and pass through a function and pass on result of function this happens anytime an item is received on any stream. onComplete if all onComplete, onError if any onError
- [ ] `withLatestFrom(Publisher,Publishers)` - for a primary stream, any time an item appears combine it with latest from other streams using function to produce new item. onComplete if all onComplete, onError if any onError
- [ ] `zip(Publishers)` - select N-th value of each stream and combine them using a function. onComplete if all onComplete, onError if any onError
- [ ] `firstEmitting(Publisher...)` or `selectFirstEmitting(Publisher...)` - wait for first publisher to emit a value, select publisher and then cancel other publishers and pass on signals from selected publisher

**Accumulating Processors** (Takes 1 or more values from a single streams and combine) (a.k.a horizontal merging operations as it combines values within streams)

- [x] `scan((accumulator, item) => {...function...}, initialValue)` a.k.a `reduce(....)` - For each value in stream pass it into accumulating function that takes current accumulated value and new value to produce new value. Initial value for accumulator is specified at startup. Final value is emitted on onComplete.

**Terminator Subscribers**

- [x] `forEach(Action)` - perform action for each value.

-----

- [ ] `delay(DelayTime)` - delay each item by DelayTime
- [ ] `delaySubscription(DelayTime)` - delay subscription of upstream by DelayTime
- [ ] `peek(Action)` - perform an action on each value that goes by
- [ ] `distinct()` - only send item first time it appears in stream. Potentially needs a very large buffer.
- [ ] `distinctInSuccession()` or `distinctUntilChanged()` - only send item first time it appears in stream. Need to buffer last.
- [ ] `sort()` - buffer all items until onComplete then apply some sorting

## TODO

* Rename project to something short ... possibly `Straz`? or `Krast` or `Streak`
