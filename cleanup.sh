#!/bin/bash
find $1 -mtime +$2 -type f -delete

