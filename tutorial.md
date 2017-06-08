# Tutorial




## Czego potrzebujesz

### &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;- JRE
### &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;- 15 minut wolnego czasu
### &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;- Troszkę chęci

## Poniższy język wspomaga działania na listach. Wspiera on kokatenację, odejmowanie list, dostęp indeksowy itd.


### Tak więć jak napisać pierwszy program w tym języku? 
Napiszmy program `hello world`. Stwórzmy plik tekstowy o nazwie `tutorial.jlist`. W pliku napiszmy kod:

```
func main(){
	return {"hello world"};	
}
```

Zapiszmy plik i wywołajmy `komendę java -jar <ściezka do interpretera> <ściezka do pliku>/tutorial.jlist main`
Program zwraca nam `[hello world]` czyli listę z łańcuchem znakowym. Teraz zadeklarujmy w funkcji `main` zmienna typu `list` o nazwie `lista`   i umieśćmy w niej nieposortowane liczby.

```
list lista={7,6,8,11,2,5,1,9};  
```

Teraz napiszmy funkcje `sort`  której argumentem będzie lista i będzie się ona nazywała `li`.Będzie ona w pętli `while` wyszukiwała najmniejszy element z pozostałych w `li` i dodawała go do nowej listy `sortedList` , odejmując go od listy `li`. Pętla kończy się w momencie wyczerpania się elementów w liście `li`. Wielkość listy możemy sprawdzać za pomocą funkcji wbudowanej `length()`  Na koniec zwróci `sortedList`. Jednak najpierw napiszmy funkcję `min`, która zwróci nam najmniejszy element z listy l. W pętli `for` przeiterujemy po liście wybierając najmniejszy element.

```
func min(list l){
	long min=l[0];
	for(long j=0;j<l.length();++j){
		if(l[j]<min){
			min=l[j];
		};	
	};
	return min;
}
```

Jak widzimy do elementów listy możemy odwoływać się za pomocą operatora `[]` podając index elementu. Elementy liczymy od `0!`
Teraz czas na wykorzystanie funkcji min w funkcji sort.

```
func sort(list li) {
	  list sortedList={};
	  while(li.length()>0){
	  	long min=min(li);
		sortedList+=min;
		li-=min;
	  };
	  return sortedList;
   }

```

Operator `+=` i  `-=` są odpowwiednikami `+/-` i `=` tzn. wyrażenia    `li+=x`  i `list=list+x` są `równowaźne`. Operator `–` usuwa z listy wszystkie elementy równe prawemu argumentowi.  Zmieniając funkcje `main` w poniższy sposób po uruchomieniu programu z tymi samymi argumentami uzyskamy wynik: 
```
[1, 2, 5, 6, 7, 8, 9, 11]
```
Nasza lista jest posortowana rosnąco gdyż szukaliśmy najmniejszego elementu i wstawialiśmy go na koniec listy, więc kolejne argumenty były coraz większe. 

```
func main(){
	list lista={7,6,8,11,2,5,1,9};  
	return sort(lista);
}
```

Napiszmy teraz `rekurencyjną` funkcjię liczenia silni. 

```
func silnia(long num){
	if(num>1){
		return num*silnia(num-1);
	};
	return 1;
}
```

Wykorzystajmy ją w ten sposób, że do posortowanej listy zamiast elementów listy będziemy wstawiać ich silnie. Zmiana polega na dodaniu do listy nie `min`, a `silni` z `min`. 
```
sortedList+=silnia(min);
```
zamiast
```
sortedList+=min;
```
Program zwraca 
```
[1, 2, 120, 720, 5040, 40320, 362880, 39916800].
```

Dokonajmy jeszcze jednej zmiany w funkcji main by zobaczyć konkatenację list za pomocą operatora +. Funkcja main wygląda teraz tak:
```
func main(){
	list lista={7,6,8,11,2,5,1,9};  
	list conclista=sort(lista)+lista;
	return {conclista};
}
```

Operator + doklei do posortowanej listy silni listę lista. Wynik jaki otrzymamy to: 
```
[[1, 2, 120, 720, 5040, 40320, 362880, 39916800, 7, 6, 8, 11, 2, 5, 1, 9]]
```
Zauważmy, że wykorzystując operator `{}` opakowaliśmy naszą skonkatenowaną listę kolejną listą.
Ostateczny kod wygląda następująco:

```
func main(){
	list lista={7,6,8,11,2,5,1,9};  
	list conclista=sort(lista)+lista;
	return {conclista};
}

func sort(list li) {
	  list sortedList={};
	  while(li.length()>0){
	  	long min=min(li);
		sortedList+=silnia(min);
		li-=min;
	  };
	  return sortedList;
   }

func min(list l){
	long min=l[0];
	for(long j=0;j<l.length();++j){
		if(l[j]<min){
			min=l[j];
		};	
	};
	return min;
}

func silnia(long num){
	if(num>1){
		return num*silnia(num-1);
	};
	return 1;
}	
```
# Czego nauczyliśmy się w tym tutorialu? 
Nauczyliśmy się pisać własne funkcje oraz wywoływać je. Dowiedzielismy się także jak działa operator + i – dla list a także wiemy już jak dostać się do dowolnego elementu listy(element może być także listą). Napisaliśmy także funkcję, która wykorzystując rekurencyję, liczy silnie. 
### Mam nadzięję, że ten tutorial czegoś Cię nauczył, a Twój czas nie poszedł na marne.

# Pozdrawiam,
## &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;RLI
