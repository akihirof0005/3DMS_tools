require 'json'
require 'open-uri'
 
File.open("snfgApi.json") do |file|
  hash = JSON.load(file)
  hash[0].each { |index, value|
    if(value) 
      name1 = (hash[1][index].sub!(/\[\]\[/, '')).sub!(/\]\{\}/, '')
      name2 =  (hash[2][index].sub!(/\[\]\[/, '')).sub!(/\]\{\}/, '')
      
      open(hash[3][index].to_s) do |file|
        open("./pdb/"+name1+".pdb", "w+b") do |out|
          out.write(file.read)
        end
      end
      open(hash[4][index]) do |file|
        open("./pdb/"+name2+".pdb", "w+b") do |out|
          out.write(file.read)
        end
      end
      
    end
  }
end