
IFS=$'\n'
for line in `ls ./pdb/*`
do
sed -i -e "/XX/d" $line
sed -i -e "/TER/d" $line
babel -h -ipdb $line -osdf /tmp/test.sdf
babel -h -isdf /tmp/test.sdf -opdb $line
done