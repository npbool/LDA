f = open("word_map.txt")
words = []
for l in f:
    index,w = l.strip().split()
    words.append(w)
f.close()

f = open("output.txt")
for l in f:
    prob = l.strip().split()
    res = [(i,float(p)) for i,p in enumerate(prob)]
    res = sorted(res, key=lambda e:-e[1])
    for w in res[:10]:
        print words[w[0]]," ",
    print ""