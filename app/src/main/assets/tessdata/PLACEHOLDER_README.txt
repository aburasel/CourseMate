OCR language data goes here.

Tesseract (tesseract4android) reads Bangla + Arabic from images using the
"Scan image" button in the batch-add dialogs. For it to work, place these two
trained-data files in THIS folder (app/src/main/assets/tessdata/):

    ben.traineddata   (Bengali)
    ara.traineddata   (Arabic)

Recommended source — the small/fast models (good size/accuracy balance):

    https://github.com/tesseract-ocr/tessdata_fast/raw/main/ben.traineddata
    https://github.com/tesseract-ocr/tessdata_fast/raw/main/ara.traineddata

Download with curl:

    curl -L -o ben.traineddata https://github.com/tesseract-ocr/tessdata_fast/raw/main/ben.traineddata
    curl -L -o ara.traineddata https://github.com/tesseract-ocr/tessdata_fast/raw/main/ara.traineddata

(For higher accuracy on photos use the "tessdata_best" repo instead; the files
are larger. The "ben.traineddata" name must match exactly — Tesseract loads by
language code "ben+ara" set in OcrHelper.kt.)

Until these files are present, the Scan button shows
"OCR language data not installed" and does nothing else. You can delete this
placeholder once the real files are added.
