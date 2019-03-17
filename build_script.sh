for PROJECT_DIR in */
do 
  cd $PROJECT_DIR
  for TASK_DIR in */
  do
      cd $TASK_DIR
      if [[ -f "gradlew" ]]
      then
          ./gradlew check
      fi
      cd ..
  done
  cd ..
done 
