use std::collections::HashMap;
use std::env;
use std::error::Error;
use std::fs;
use std::fs::File;
use std::io::{BufRead, BufReader};
use std::path::Path;

#[derive(Debug)]
struct Point3d {
    x: f64,
    y: f64,
    z: f64,
}

//外積
fn veprod(v1: &Point3d, v2: &Point3d) -> Point3d {
    let x: f64 = v1.y * v2.z - v2.y * v1.z;
    let y: f64 = v1.z * v2.x - v2.z * v1.x;
    let z: f64 = v1.x * v2.y - v2.x * v1.y;
    return Point3d { x: x, y: y, z: z };
}
//内積
fn scprod(v1: &Point3d, v2: &Point3d) -> f64 {
    let ret: f64 = v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
    return ret;
}
//差（座標をベクトルにする）
fn sub(p1: &Point3d, p2: &Point3d) -> Point3d {
    let x: f64 = p2.x - p1.x;
    let y: f64 = p2.y - p1.y;
    let z: f64 = p2.z - p1.z;
    return Point3d { x: x, y: y, z: z };
}
//文字列から文字数分取り出す
fn kiridashi(text: String, start: usize, end: usize) -> String {
    let begin = text.char_indices().nth(start).unwrap().0;
    let end = text.char_indices().nth(end).unwrap().0;
    let ret = &text[begin..end];
    return ret.trim().to_owned();
}

fn connect_info(path: String) -> Vec<String> {
    let path = Path::new(&path);
    let display = path.display();

    let f = match File::open(&path) {
        Err(why) => panic!("couldn't open {}: {}", display, why.description()),
        Ok(file) => file,
    };

    //開いファイルを使ってBufReaderで読み出す
    let reader = BufReader::new(f);
    let mut list: Vec<String> = Vec::new();

    //一行ずつ取り出しながらベクターに格納していく
    for line in reader.lines() {
        let s = line.unwrap();
        if s == "" || s == "END" {
            continue;
        }
        let items: Vec<&str> = s.split_whitespace().collect();
        let rec: String = kiridashi(s.to_string(), 0, 3);

        if "CON" == rec && items.len() == 4 {
            list.push(s);
        } else {
            continue;
        }
    }
    return list;
}

fn main() {
    let args: Vec<String> = env::args().collect();
    let name = &args[1];

    let path = format!(
        "{}{}{}{}{}{}",
        "/home/skit/resarch/extractData/out/shape_", name, "/shape/out/", name, "/", name
    );

    let c_list = connect_info(path);

    let target = format!(
        "{}{}{}{}{}{}",
        "/home/skit/resarch/extractData/out/shape_", name, "/shape/out/", name, "/", "out/"
    );
    let mut ret: HashMap<String, i32> = HashMap::new();
    ret.insert("1C4".to_string(), 0);
    ret.insert("4C1".to_string(), 0);
    for p in fs::read_dir(target).unwrap() {
        let path = p.unwrap().path().display().to_string();
        //   let mut data = String::new();
        let mut map: HashMap<String, String> = HashMap::new();

        let file = File::open(path).expect("cannot open file");
        let file = BufReader::new(file);
        for line in file.lines() {
            let s: String = line.unwrap();

            if s == "" || s == "END" {
                continue;
            }
            let items: Vec<&str> = s.split_whitespace().collect();
            let rec: String = kiridashi(s.clone(), 0, 3);
            //println!("{:#?}", rec);
            if "ATO" == rec {
                let str3: String = items[1].to_string();
                let sa = s.clone();
                map.insert(str3, sa);
            } else {
                continue;
            }
        }

        let mut a1: Vec<&str>;
        let mut a2: Vec<&str>;
        let mut a3: Vec<&str>;
        let mut items: Vec<&str>;

        for str1 in c_list.iter() {
            items = str1.split_whitespace().collect();
            let t = items[1];
            let text = &map[t];
            a1 = text.split_whitespace().collect();
            let t = items[2];
            let text = &map[t];
            a2 = text.split_whitespace().collect();
            let t = items[3];
            let text = &map[t];
            a3 = text.split_whitespace().collect();
            //println!("{:#?} {} {} {}", str1, a1[2], a2[2],a3[2]);
            if !(a1[2] == "O" && a2[2] == "C" && a3[2] == "C") {
                continue;
            }
            //println!("{:#?} {} {} {}", str1, a1[2], a2[2],a3[2]);
            //println!("{}",map[&items[1]]);
            let x1: f64 = kiridashi(map[items[1]].to_string(), 30, 38)
                .parse()
                .unwrap();
            let y1: f64 = kiridashi(map[&*items[1]].to_string(), 38, 46)
                .parse()
                .unwrap();
            let z1: f64 = kiridashi(map[&*items[1]].to_string(), 46, 54)
                .parse()
                .unwrap();
            let po: Point3d = Point3d {
                x: x1,
                y: y1,
                z: z1,
            };
            //   println!("O\t{:?}", &po);

            //println!("{}", map[&items[2]]);
            let x2: f64 = kiridashi(map[&*items[2]].to_string(), 30, 38)
                .parse()
                .unwrap();
            let y2: f64 = kiridashi(map[&*items[2]].to_string(), 38, 46)
                .parse()
                .unwrap();
            let z2: f64 = kiridashi(map[&*items[2]].to_string(), 46, 54)
                .parse()
                .unwrap();
            let pc1: Point3d = Point3d {
                x: x2,
                y: y2,
                z: z2,
            };
            //      println!("C1\t{:?}", &pc1);

            //println!("{}", map[&items[3]]);
            let x3: f64 = kiridashi(map[&*items[3]].to_string(), 30, 38)
                .parse()
                .unwrap();
            let y3: f64 = kiridashi(map[&*items[3]].to_string(), 38, 46)
                .parse()
                .unwrap();
            let z3: f64 = kiridashi(map[&*items[3]].to_string(), 46, 54)
                .parse()
                .unwrap();
            let pc5: Point3d = Point3d {
                x: x3,
                y: y3,
                z: z3,
            };
            //      println!("C5\t{:?}", &pc5);
            let c1_num: i32 = items[2].parse().unwrap();
            let c5_num: i32 = items[3].parse().unwrap();
            let c3_num: i32 = (c1_num + c5_num) / 2;
            let c3_str: &str = &c3_num.to_string();
            let x4: f64 = kiridashi(map[&*c3_str].to_string(), 30, 38)
                .parse()
                .unwrap();
            let y4: f64 = kiridashi(map[&*c3_str].to_string(), 38, 46)
                .parse()
                .unwrap();
            let z4: f64 = kiridashi(map[&*c3_str].to_string(), 46, 54)
                .parse()
                .unwrap();
            let pc3: Point3d = Point3d {
                x: x4,
                y: y4,
                z: z4,
            };
            //      println!("C3\t{:?}", &pc3);
            let v1: Point3d = sub(&po, &pc1);
            let v2: Point3d = sub(&po, &pc5);
            let vo3: Point3d = sub(&po, &pc3);
            //      println!("vOC3\t{:?}", &vo3);
            let vp: Point3d = veprod(&v1, &v2);
            //      println!("veprod\t{:?}", &vp);
            let v: f64 = scprod(&vo3, &vp);
            if v < 0.0 {
                //println!("{}", "1C4");
                let num = &ret["4C1"] + 1;
                ret.insert("4C1".to_string(), num);
            } else {
                //println!("{}", "4C1");
                let num = &ret["1C4"] + 1;
                ret.insert("1C4".to_string(), num);
            }
        }
    }
    println!("{} 4C1:{} 1C4:{}", name, ret["4C1"], ret["1C4"]);
}
