**Day 1 Part 2**

[_and figuring out a few more things_]

Using the numbers twice

println!("Day 1 Part 1 {} Part 2 {}", day1::part1(numbers), day1::part2(numbers));

When I run this I see:

```
$ cargo run
   Compiling aoc2021 v0.1.0 (/Users/andrewwhitehouse/code/aoc2021)
error[E0382]: use of moved value: `numbers`
 --> src/main.rs:9:77
  |
8 |     let numbers = day1::parse(input);
  |         ------- move occurs because `numbers` has type `Vec<u32>`, which does not implement the `Copy

` trait
9 |     println!("Day 1 Part 1 {} Part 2 {}", day1::part1(numbers), day1::part2(numbers));
  |                                                       -------               ^^^^^^^ value used here a
fter move
  |                                                       |
  |                                                       value moved here

For more information about this error, try `rustc --explain E0382`.
error: could not compile `aoc2021` due to previous error
$
```

That's a slightly intimidating error message. :grimacing: 

The simplest thing is to clone:

```
println!("Day 1 Part 1 {} Part 2 {}", day1::part1(numbers.clone()), day1::part2(numbers.clone()));
```

This works, but I wonder if it's the _right_ way to do it.

I don't have any significant memory or performance issues. So I could probably stop here.

Let's see what happens if we try to use the same data structure.

The data structure is allocated on this line in main:

```
let numbers = day1::parse(input);
```

And it's a Vector of unsigned 32-bit integers, Vec<U32>.

I'm assuming I need to convert the parameters to be references.

Let's start changing things to use references.

In this `diff` output the first line prefixed with a minus is the before and the line prefixed with the plus is after the changes.

`day1.rs`

```
diff --git a/src/day1.rs b/src/day1.rs
index 89e91df..6b4e4d1 100644
--- a/src/day1.rs
+++ b/src/day1.rs
@@ -1,6 +1,6 @@
 use std::str::FromStr;


-fn count_increases(numbers: Vec<u32>) -> u32 {
+fn count_increases(numbers: &Vec<u32>) -> u32 {
     let mut increases = 0;
     for index in 1..numbers.len() {
         if numbers[index] > numbers[index-1] {
@@ -15,11 +15,11 @@ pub fn parse(input: String) -> Vec<u32> {
     numbers
 }

-pub fn part1(numbers: Vec<u32>) -> u32 {
+pub fn part1(numbers: &Vec<u32>) -> u32 {
     count_increases(numbers)
 }

-fn sliding_window_sum(numbers: Vec<u32>) -> Vec<u32> {
+fn sliding_window_sum(numbers: &Vec<u32>) -> Vec<u32> {
     let mut result = Vec::new();
          let mut sum = numbers[0] + numbers[1] + numbers[2];
     result.push(sum);
@@ -30,8 +30,8 @@ fn sliding_window_sum(numbers: Vec<u32>) -> Vec<u32> {
     return result;
 }

-pub fn part2(numbers: Vec<u32>) -> u32 {
-    count_increases(sliding_window_sum(numbers))
+pub fn part2(numbers: &Vec<u32>) -> u32 {
+    count_increases(&sliding_window_sum(numbers))
 }
```

`main.rs`

```
diff --git a/src/main.rs b/src/main.rs
index 2235578..d555a3e 100644
--- a/src/main.rs
+++ b/src/main.rs
@@ -6,7 +6,7 @@ fn solve_day1() {
     let input = fs::read_to_string("input/day1.txt")
         .expect("Something went wrong reading the file");
     let numbers = day1::parse(input);
-    println!("Day 1 Part 1 {} Part 2 {}", day1::part1(numbers.clone()), day1::part2(numbers.clone()));
+    println!("Day 1 Part 1 {} Part 2 {}", day1::part1(&numbers), day1::part2(&numbers));
 }
```

So that's changes in five places.

I wonder if it's worth it. The extra `clone` is not an issue for my standalone programme; it probably would be for code where optimal performance and memory usage were issues.

But as a general rule, using references seems to be a way to avoid the restrictions on passing data structures around, and the ownership of those data structures, which Rust controls tightly to ensure that memory is allocated and de-allocated correctly.

There may be a rule of thumb somewhere that experienced Rust developers recognise, that you should allocate your variables in <certain situations> and use references otherwise.

I am not a big fan of languages that impose extra cognitive load on the programmer. But I can see how they are helpful as a means to control memory usage when that's something that's important.

What are my reflections from this post? 

1. That keeping the Rust book focused on applications it's well suited for, and I think that blockchain smart contracts are one of those applications.
2. Since the ownership rules around data have such an impact on how we code (even with relatively simple programmes) I need to dig more into example scenarios.
3. I still haven't answered _when to use a String, and when to use a &str_. And couldn't someone have come up with a more accessible name for `&str`? I find some of the terminology in the Rust space quite unintuitive and I wonder how good the Rust core team will be at responding to [community feedback](https://developers.slashdot.org/story/21/11/27/0123211/rusts-moderation-team-resigns-to-protest-unaccountable-core-team#:~:text=On%20Monday%20morning%20the%20moderation,Team%20placing%20themselves%20unaccountable%20to).
4. Introducing modules was a useful addition, because programmes usually consist of more than one file!

Here's the output after the refactor:

```
$ cargo run
    Finished dev [unoptimized + debuginfo] target(s) in 0.00s
     Running `target/debug/aoc2021`
Day 1 Part 1 1342 Part 2 1378
$
```