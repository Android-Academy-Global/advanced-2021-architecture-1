path := ./

detekt:
	$(path)gradlew detektAll --no-configuration-cache
