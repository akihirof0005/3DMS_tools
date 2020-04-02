extern crate serde;
extern crate serde_derive;
extern crate serde_json;
use std::fs;

fn main() -> Result<(), Box<std::error::Error>> {
    let target = "/home/skit/resarch/dist";
    let mut files: Vec<String> = Vec::new();
    let mut list: Vec<Vec<Vec<f64>>> = Vec::new();

    let mut max = 10;
    for p in fs::read_dir(target).unwrap() {
        let path = p.unwrap().path().display().to_string();

        let s: String = fs::read_to_string(&path)?;
        let str: String = s.replace("null,", "");
        let v: Vec<Vec<f64>> = serde_json::from_str(&str).unwrap();

        for item in &v {
            let n = item[0] as i8;
            if (max < n) {
                max = n;
            }
        }
        let mut d: f64 = 100.0;

        for item in &v {
            let mut dist: f64 = 0.0;
            let mut n = 3 as i8;
            if (item[0] as i8 != 0) {
                n = item[0] as i8;
            }
            for i in (n + 1)..(max + 1) {
                let num = i as f64;
                dist = dist + 0.1 * ((num) * (num - 1.0) * (num - 2.0) * 1.2 / 6.0).log10() + 1.0;
            }
            if item[1] == 0.0 {
                let num = 3 as f64;
                dist = dist + 0.1 * ((num) * (num - 1.0) * (num - 2.0) * 1.2 / 6.0).log10() + 1.0;
            } else {
                dist = dist + item[1];
            }
            if d > dist {
                d = dist;
            }
        }
        let dist: f64 = d;
        let mut name: String = path.replacen(target, "", 1);
        name.retain(|c| c != '/');
        let mut l: Vec<&str> = name.split('.').collect();
        let name = l[0].to_string();
        l = name.split('_').collect();
        println!("{},{},{}", l[0], l[1], dist);
    }
    Ok(())
}
