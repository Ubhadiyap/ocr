# Sets the minimum version of CMake required to build your native library.
# This ensures that a certain set of CMake features is available to
# your build.

cmake_minimum_required(VERSION 3.4.1)


# build native_app_glue as a static lib
#add_library(app-glue STATIC
#           ${ANDROID_NDK}/sources/android/native_app_glue/android_native_app_glue.c)

# now build app's shared lib
#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")
link_directories(/usr/local/lib)
set(pathToOpenCv /Users/stephineosoro/Downloads/OpenCV-android-sdk)

# Specifies a library name, specifies whether the library is STATIC or
# SHARED, and provides relative paths to the source code. You can
# define multiple libraries by adding multiple add.library() commands,
# and CMake builds them for you. When you build your app, Gradle
# automatically packages shared libraries with your APK.

include_directories(${pathToOpenCv}/sdk/native/jni/include)
add_library( # Specifies the name of the library.
             native-lib

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/jni/nativeclass.c )

add_library( # Specifies the name of the library.
             mixed_sample

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/jni/jni_part.cpp )



#target_include_directories(native-lib PRIVATE
#               ${ANDROID_NDK}/sources/android/native_app_glue)


# add lib dependencies
# Include libraries needed for hello-jni lib
target_link_libraries(native-lib
                      mixed_sample
                      android
                      log
                      libopencv_contrib libopencv_legacy libopencv_ml libopencv_stitching libopencv_nonfree libopencv_objdetect libopencv_videostab libopencv_calib3d libopencv_photo libopencv_video libopencv_features2d libopencv_highgui libopencv_androidcamera libopencv_flann libopencv_imgproc libopencv_core
                      )



