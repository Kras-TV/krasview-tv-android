#! /bin/sh

checkfail()
{
    if [ ! $? -eq 0 ];then
        echo "$1"
        exit 1
    fi
}

if [ ! -d "vlc-android" ]; then
    echo "VLC-android source not found, cloning"
    git clone https://github.com/anutakay/vlc-android-for-krasview.git
    checkfail "vlc source: git clone failed"
fi

cd vlc-android
./compile.sh -l
cd ..

