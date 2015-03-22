using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ChordFingerFinder
{
    class CNoteFingerPosition
    {
        public CFingerPosition FingerPosition { get; private set; }
        public int Note { get; private set; }

        public CNoteFingerPosition(CFingerPosition cPosition, int eNote)
        {
            FingerPosition = new CFingerPosition(cPosition);
            Note = eNote;
        }

        public CNoteFingerPosition(CNoteFingerPosition other)
        {
            FingerPosition = new CFingerPosition(other.FingerPosition);
            Note = other.Note;
        }

        public bool AdvanceFrets(int nFrets)
        {
            if (!FingerPosition.AdvanceFrets(nFrets))
            {
                return false;
            }

            if(nFrets > 0)
            {
                if (FingerPosition.Fret + FingerPosition.SameNoteFretDifferenceOnString(FingerPosition.String, FingerPosition.String+1) >= 0)
                {
                    if (!FingerPosition.NextString())
                    {
                        return FingerPosition.PreviousOctave();
                    }
                }
            }
            else
            {
                if (FingerPosition.Fret % FingerPosition.SameNoteFretDifferenceOnString(FingerPosition.String, FingerPosition.String - 1) == 0)
                {
                    // Can fail.
                    FingerPosition.PreviousString();
                }
            }

            int nCurrNote = (int)Note;
            int nNumOfNotes = (int)ENotes.NumberOfNotes;

            // Advance notes
            Note = ((nNumOfNotes + ((nCurrNote + nFrets) % nNumOfNotes)) % nNumOfNotes);

            return true;
        }

        public bool NextString()
        {
            return FingerPosition.NextString();
        }

        public bool PreviousString()
        {
            return FingerPosition.PreviousString();
        }
    };
}
