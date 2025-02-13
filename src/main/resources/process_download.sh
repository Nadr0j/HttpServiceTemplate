#!/bin/sh
# Usage: ./process_download.sh <video-file>

# ----- PARAMETERS (Constants) -----
NUM_SEGMENTS=20       # Number of segments for the preview GIF
SEGMENT_DURATION=1    # Duration (in seconds) of each segment
FPS=30                # Frame rate for the output GIF
SCALE_WIDTH=640       # Width for scaling (height auto-adjusts)
# ------------------------------------

# Retrieve input file info.
file="$1"
ext=$(echo "${file##*.}" | tr '[:upper:]' '[:lower:]')
base=$(basename "$file" ".$ext")
dir=$(dirname "$file")

# Use the input file's directory as the base output directory.
BASE_OUTPUT_DIR="$dir"
THUMBNAIL_DIR="${BASE_OUTPUT_DIR}/thumbnail"
VIDEO_DIR="${BASE_OUTPUT_DIR}/video"
PREVIEW_DIR="${BASE_OUTPUT_DIR}/preview"

# DEBUG ECHOS
echo "file: ${file}"
echo "ext: ${ext}"
echo "base: ${base}"
echo "dir: ${dir}"
echo "Thumbnail dir: ${THUMBNAIL_DIR}"
echo "Video dir: ${VIDEO_DIR}"
echo "Preview dir: ${PREVIEW_DIR}"
# DEBUG ECHOS

# Ensure target directories exist inside the base output directory.
mkdir -p "$THUMBNAIL_DIR" "$VIDEO_DIR" "$PREVIEW_DIR"

# Supported video extensions (space-separated)
SUPPORTED_EXTENSIONS="mp4 mkv webm"

# Verify the file is a supported video.
case "$SUPPORTED_EXTENSIONS" in
  *"$ext"*) ;;
  *) echo "Error: Unsupported file extension '$ext'. Expecting one of: ${SUPPORTED_EXTENSIONS}." ; exit 1 ;;
esac

# Obtain the video duration in seconds.
duration=$(ffprobe -v error -select_streams v:0 \
  -show_entries format=duration -of csv=p=0 "$file")

# Calculate the interval so that NUM_SEGMENTS segments of SEGMENT_DURATION seconds are evenly spaced.
# Last segment starts at (duration - SEGMENT_DURATION)
interval=$(echo "scale=6; ($duration - $SEGMENT_DURATION) / ($NUM_SEGMENTS - 1)" | bc -l)

# Build filter_complex to trim NUM_SEGMENTS segments.
filter_complex=""
for i in $(seq 0 $(($NUM_SEGMENTS - 1))); do
  start=$(echo "scale=6; $i * $interval" | bc -l)
  filter_complex="${filter_complex}[0:v]trim=start=${start}:duration=${SEGMENT_DURATION},setpts=PTS-STARTPTS[v${i}];"
done

# Concatenate the segments.
inputs=""
for i in $(seq 0 $(($NUM_SEGMENTS - 1))); do
  inputs="${inputs}[v${i}]"
done
filter_complex="${filter_complex}${inputs}concat=n=${NUM_SEGMENTS}:v=1:a=0,fps=${FPS},scale=${SCALE_WIDTH}:-1:flags=lanczos[v]"

# Generate the GIF preview from the concatenated segments inside the preview directory.
ffmpeg -i "$file" -filter_complex "$filter_complex" -map "[v]" "${PREVIEW_DIR}/${base}.gif"

# Move the video file into the video subdirectory.
mv "$file" "$VIDEO_DIR/"

# Locate and move the associated thumbnail (if it exists) into the thumbnail subdirectory.
thumb_jpg="${dir}/${base}.jpg"
thumb_png="${dir}/${base}.png"
if [ -f "$thumb_jpg" ]; then
  mv "$thumb_jpg" "$THUMBNAIL_DIR/"
elif [ -f "$thumb_png" ]; then
  mv "$thumb_png" "$THUMBNAIL_DIR/"
else
  echo "No thumbnail found for ${file}"
fi
