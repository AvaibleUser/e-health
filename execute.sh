project='frontdoor'

execute_run() {
  printf "Ejecutando gradle bootRun en ${project}"
  (env $(cat "${project}/.env" | xargs) ./gradlew clean ":${project}:bootRun --debug")
}

execute_error() {
  printf "Las opciones disponibles son:
          -p para indicar el proyecto a ejecutar"
  exit 1
}

while getopts 'p:' flag; do
  case "${flag}" in
    p) project="${OPTARG}" ;;
    *) execute_error ;;
  esac
done

execute_run
